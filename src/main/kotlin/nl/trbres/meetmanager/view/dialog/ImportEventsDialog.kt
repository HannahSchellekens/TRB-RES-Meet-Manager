package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Stroke
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class ImportEventsDialog(ownerWindow: Window?) : Dialog<String>() {

    private lateinit var txtaContent: TextArea

    init {
        title = "Programma importeren"
        headerText = "Voer nieuwe programma's toe door een set spreadsheet cellen\nin de textbox te plakken."
        contentText = """Formaat (per programma een nieuwe regel, [dit is een cel]):
            |[NxM] [SLAG] [CATEGORIE] [m/v/x]
            |...
            |
            |Voorbeeld:
            |1x200  BACKSTROKE  SENIOREN_OPEN   m
            |4x100  MEDLEY      JUNIOREN_2      v
            |4x50   FREESTYLE   SENIOREN_OPEN   x
            |
            |Mogelijke categorieën zijn:
            |${State.meet?.ageSet?.ages?.toList()?.chunked(6)?.joinToString("\n") { chunk -> chunk.joinToString(", ") { it.id }}}
            |
            |Mogelijke slagen zijn:
            |${Stroke.values().joinToString(", ") { it.name }}
            |""".trimMargin()
        dialogPane.minWidth = 350.0
        dialogPane.maxWidth = 500.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it != ButtonType.OK) {
                return@Callback null
            }
            return@Callback txtaContent.text
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