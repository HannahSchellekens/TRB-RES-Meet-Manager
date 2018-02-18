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
import nl.trbres.meetmanager.util.isNull
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class NewSwimmerDialog(ownerWindow: Window?) : Dialog<Swimmer>() {

    private lateinit var txtName: TextField
    private lateinit var cboxAgeGroup: ComboBox<AgeGroup>
    private lateinit var cboxCategory: ComboBox<Category>
    private lateinit var cboxClub: ComboBox<Club>

    init {
        title = "Nieuwe zwemmer"
        headerText = "Nieuwe zwemmer toevoegen"
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
                    cboxClub.selectedItem?.mapToClub() ?: Club.NO_CLUB
            )
        }

        dialogPane.content = form {
            fieldset {
                field("Naam zwemmer") {
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
                cboxCategory.selectedItem.isNull()
    }
}