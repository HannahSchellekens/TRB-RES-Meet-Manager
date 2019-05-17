package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.*
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.filter.AgeGroupFilter
import nl.trbres.meetmanager.filter.SwimmerFilter
import nl.trbres.meetmanager.model.AgeGroup
import nl.trbres.meetmanager.util.isNaturalNumber
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class EndResultDialog(ownerWindow: Window?) : Dialog<EndResultDialogResult>() {

    private lateinit var txtEvents: TextField
    private lateinit var txtConversion: TextField
    private lateinit var checkConvert: CheckBox
    private lateinit var cboxAgeFilter: ComboBox<AgeGroup>
    private lateinit var checkAgeFilter: CheckBox

    init {
        title = "Eindresultaat genereren"
        headerText = "Geef de programmnummers op waarvan je de einduitslag wilt genereren."
        dialogPane.minWidth = 350.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it != ButtonType.OK) {
                return@Callback null
            }

            val filter = if (checkAgeFilter.isSelected) {
                AgeGroupFilter(cboxAgeFilter.selectedItem!!)
            }
            else SwimmerFilter.NO_FILTER

            EndResultDialogResult(
                    txtEvents.text,
                    if (checkConvert.isSelected) {
                        txtConversion.text.toInt()
                    }
                    else null,
                    filter
            )
        }

        dialogPane.content = form {
            fieldset {
                field("Programmanummers gescheiden door komma's") {
                    txtEvents = textfield {
                        textProperty().addListener { _ -> validate(okButton) }
                    }
                }
                hbox {
                    label("Tijden omzetten: ") {
                        prefHeight = 25.0
                    }
                    checkConvert = checkbox {
                        isSelected = false
                        prefHeight = 25.0
                        selectedProperty().addListener { _ -> validate(okButton) }
                    }
                    label("")
                    txtConversion = textfield("50") {
                        prefHeight = 25.0
                        prefWidth = 64.0
                        textProperty().addListener { _ -> validate(okButton) }
                    }
                }
                hbox {
                    label("Leeftijd filter: ") {
                        prefHeight = 25.0
                    }
                    checkAgeFilter = checkbox {
                        isSelected = false
                        prefHeight = 25.0
                        selectedProperty().addListener { _ -> validate(okButton) }
                    }
                    label("")
                    cboxAgeFilter = combobox {
                        prefHeight = 25.0
                        prefWidth = 250.0
                        items.addAll(State.meet!!.ageSet.ages)
                        selectionModel.selectedItemProperty().addListener { _ -> validate(okButton) }
                    }
                }
            }
        }

        txtConversion.disableProperty().bind(!checkConvert.selectedProperty())
        cboxAgeFilter.disableProperty().bind(!checkAgeFilter.selectedProperty())

        runLater {
            txtEvents.requestFocus()
        }
    }

    /**
     * Validates the input, and if it is correct, enables the okButton. Otherwise it disables it.
     */
    private fun validate(okButton: Node) {
        val eventsCorrect = txtEvents.text.matches(Regex("\\d+(,\\d+)*"))

        val conversionCorrect = try {
            txtConversion.text.isNaturalNumber() && txtConversion.text.toInt() > 0 && txtConversion.text.toInt() % 25 == 0
        }
        catch (e: Exception) {
            false
        }

        if (checkAgeFilter.isSelected && cboxAgeFilter.selectionModel.selectedItem == null) {
            okButton.isDisable = true
            return
        }

        okButton.isDisable = !eventsCorrect || (checkConvert.isSelected && !conversionCorrect)
    }
}

/**
 * @author Hannah Schellekens
 */
class EndResultDialogResult(

        /**
         * A list with all event numbers to generate the end result of seperated by commas.
         */
        val events: String,

        /**
         * When not null, contains the distance to which the result must be converted to.
         */
        val convertTo: Int?,

        /**
         * The filter that select what swimmers will end up in the end result.
         */
        val filter: SwimmerFilter = SwimmerFilter.NO_FILTER
)