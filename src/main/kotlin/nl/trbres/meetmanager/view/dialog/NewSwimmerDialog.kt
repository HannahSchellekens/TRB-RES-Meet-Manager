package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextField
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.AgeGroup
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.util.firstUpper
import nl.trbres.meetmanager.util.isNull
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class NewSwimmerDialog(protected val ownerWindow: Window?) : Dialog<Swimmer>() {

    protected lateinit var txtName: TextField
    protected lateinit var cboxAgeGroup: ComboBox<AgeGroup>
    protected lateinit var cboxCategory: ComboBox<Category>
    protected lateinit var cboxClub: ComboBox<Club>
    protected lateinit var txtYear: TextField
    protected lateinit var fieldset: Fieldset

    protected open val swimmerText: String = "zwemmer"

    init {
        title = "${swimmerText.firstUpper()} toevoegen"
        headerText = "${swimmerText.firstUpper()} toevoegen"
        dialogPane.minWidth = 350.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it != ButtonType.OK) {
                return@Callback null
            }

            Swimmer(
                    txtName.text.trim(),
                    cboxAgeGroup.selectedItem!!,
                    cboxCategory.selectedItem!!,
                    cboxClub.selectedItem?.mapToClub() ?: Club.NO_CLUB,
                    txtYear.text.toIntOrNull()
            )
        }

        dialogPane.content = form {
            fieldset = fieldset {
                field("Naam $swimmerText") {
                    txtName = textfield {
                        textProperty().addListener { _ -> validate(okButton) }
                    }
                }
                field("Leeftijdscategorie") {
                    cboxAgeGroup = combobox {
                        items = State.meet!!.ageSet.ages.toList().observable()
                        selectionModel.selectedItemProperty().addListener { _ -> validate(okButton) }
                    }
                }
                field("M/V") {
                    cboxCategory = combobox {
                        items = listOf(Category.FEMALE, Category.MALE).observable()
                        selectionModel.selectedItemProperty().addListener { _ -> validate(okButton) }
                    }
                }
                field("Vereniging (optioneel)") {
                    cboxClub = combobox {
                        items = (listOf(Club.NO_CLUB) + (State.meet?.clubs ?: emptyList())).observable()
                    }
                }
                field("Geboortejaar (optioneel)") {
                    txtYear = textfield {
                        textProperty().addListener { _ -> validate(okButton) }
                    }
                }
            }
        }

        runLater {
            txtName.requestFocus()
        }
    }

    /**
     * Validates the input, and if it is correct, enables the okButton. Otherwise it disables it.
     */
    private fun validate(okButton: Node) {
        okButton.isDisable = txtName.text.isNullOrBlank() ||
                cboxAgeGroup.selectedItem.isNull() ||
                cboxCategory.selectedItem.isNull() ||
                (txtYear.text.isNotEmpty() && txtYear.text.toIntOrNull().isNull())
    }
}