package nl.trbres.meetmanager.view

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.util.isNaturalNumber
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class EndResultDialog(ownerWindow: Window?) : Dialog<EndResultDialogResult>() {

    private lateinit var txtEvents: TextField
    private lateinit var txtConversion: TextField
    private lateinit var cboxConvert: CheckBox

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

            EndResultDialogResult(
                    txtEvents.text,
                    if (cboxConvert.isSelected) {
                        txtConversion.text.toInt()
                    }
                    else null
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
                    cboxConvert = checkbox {
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
            }
        }

        txtConversion.disableProperty().bind(!cboxConvert.selectedProperty())

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

        okButton.isDisable = !eventsCorrect || (cboxConvert.isSelected && !conversionCorrect)
    }
}

/**
 * @author Ruben Schellekens
 */
class EndResultDialogResult(

        /**
         * A list with all event numbers to generate the end result of seperated by commas.
         */
        val events: String,

        /**
         * When not null, contains the distance to which the result must be converted to.
         */
        val convertTo: Int?
)