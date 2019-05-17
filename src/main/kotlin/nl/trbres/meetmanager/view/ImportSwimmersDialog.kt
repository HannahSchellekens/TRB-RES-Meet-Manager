package nl.trbres.meetmanager.view

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
open class ImportSwimmersDialog(ownerWindow: Window?) : Dialog<String>() {

    private lateinit var txtaContent: TextArea

    init {
        title = "Zwemmers importeren"
        headerText = "Voer meerdere nieuwe zwemmers toe door een set spreadsheet cellen\nin de textbox te plakken."
        contentText = """Formaat (per zwemmer een nieuwe regel, [dit is een cel]):
            |[m/v] [Naam zwemmer 1] [LEEFTIJD] [Clubnaam] [Jaar]
            |[m/v] [Naam zwemmer 2] [LEEFTIJD] [Clubnaam] [Jaar]
            |...
            |
            |Voorbeeld:
            |m	Henk-Jan Vissers	JUNIOREN	TRB-RES
            |v	Ellie de Jong		SENIOREN	TRB-RES
            |
            |Let op dat er een club met de gegeven naam geregistreerd moet zijn voordat
            |de clubs succesvol kunnen worden toegewezen.
            |
            |Het geboortejaar is optioneel en kan dus weggelaten worden.
            |
            |""".trimMargin()
        dialogPane.minWidth = 400.0
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