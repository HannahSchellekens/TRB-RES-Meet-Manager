package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.model.DisqualificationCode
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class ChooseDisqualificationCodeDialog(
        ownerWindow: Window?,
        customMessage: String = "Kies een diskwalificatie."
) : Dialog<DisqualificationCode?>() {

    private lateinit var txtSearch: TextField
    private lateinit var tvwDisqualifications: TableView<DisqualificationCode>

    init {
        title = "Selecteer diskwalificatie"
        headerText = customMessage
        dialogPane.minWidth = 750.0
        initOwner(ownerWindow)

        val deleteButton = ButtonType("Geen diskwalificatie")
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL, deleteButton)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it == ButtonType.CANCEL || it == deleteButton) {
                return@Callback null
            }

            tvwDisqualifications.selectedItem
        }

        dialogPane.content = borderpane {
            top {
                txtSearch = textfield {
                    promptText = "Zoeken..."
                    textProperty().addListener { _ -> search() }
                }
            }

            center {
                tvwDisqualifications = tableview {
                    column("Code", DisqualificationCode::code) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.05))
                        isResizable = true
                    }

                    column("Omschrijving", DisqualificationCode::message) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.95))
                        isResizable = true
                    }

                    onSelectionChange { validate(okButton) }
                    items = DisqualificationCode.values().toList().observable()

                    onDoubleClick {
                        if (validate(okButton)) {
                            result = tvwDisqualifications.selectedItem
                            close()
                        }
                    }
                }

                runLater {
                    txtSearch.requestFocus()
                }
            }
        }
    }

    /**
     * Updates search.
     */
    private fun search() {
        val codes = DisqualificationCode.values().toMutableList()
        if (!txtSearch.text.isNullOrBlank()) {
            codes.removeIf {
                val query = txtSearch.text.toLowerCase()
                !it.code.toLowerCase().contains(query) && !it.message.toLowerCase().contains(query)
            }
        }
        tvwDisqualifications.items = codes.observable()
    }

    /**
     * Validates input and enables the okButton only if the input is valid.
     *
     * @return `true` when valid, `false` when invalid.
     */
    private fun validate(okButton: Node): Boolean {
        okButton.isDisable = tvwDisqualifications.selectedItem == null
        return !okButton.isDisable
    }
}