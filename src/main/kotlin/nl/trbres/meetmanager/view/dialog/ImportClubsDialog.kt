package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.stage.Window
import javafx.util.Callback
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class ImportClubsDialog(ownerWindow: Window?) : Dialog<Set<String>>() {

    private lateinit var txtaContent: TextArea

    init {
        title = "Verenigingen importeren"
        headerText = "Voer meerdere nieuwe zwemmers toe door een set spreadsheet cellen\nin de textbox te plakken."
        contentText = """Plaats iedere vereniging op een nieuwe regel.
            |
            |""".trimMargin()
        dialogPane.minWidth = 350.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback { button ->
            if (button != ButtonType.OK) {
                return@Callback null
            }
            return@Callback txtaContent.text.split("\n").asSequence()
                    .filter { it.isNotBlank() }
                    .toSet()
        }

        dialogPane.content = borderpane {
            top {
                label(contentText)
            }

            center {
                txtaContent = textarea {
                    textProperty().addListener { _ -> validate(okButton) }
                }
            }
        }

        runLater {
            txtaContent.requestFocus()
        }
    }

    /**
     * Validates the input, and if it is correct, enables the okButton. Otherwise it disables it.
     */
    private fun validate(okButton: Node) {
        okButton.isDisable = txtaContent.text.isNullOrBlank()
    }
}