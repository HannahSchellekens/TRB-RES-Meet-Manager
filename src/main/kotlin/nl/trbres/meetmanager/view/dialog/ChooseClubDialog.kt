package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Club
import tornadofx.*
import java.util.*

/**
 * @author Hannah Schellekens
 */
open class ChooseClubDialog(
        ownerWindow: Window?,
        customMessage: String = "Kies een vereniging.",
        allowAllClubs: Boolean = false
) : Dialog<List<Club>>() {

    private lateinit var txtSearch: TextField
    private lateinit var tvwClubs: TableView<Club>

    init {
        title = "Selecteer vereniging"
        headerText = customMessage
        dialogPane.minWidth = 500.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        val allClubsButton = ButtonType("Alle verenigingen")
        if (allowAllClubs) {
            dialogPane.buttonTypes.add(allClubsButton)
        }

        resultConverter = Callback {
            // No clubs.
            if (it == ButtonType.CANCEL) return@Callback null

            // All clubs.
            if (it == allClubsButton) {
                return@Callback tvwClubs.items.filterNotNull()
            }

            // One club.
            listOfNotNull(tvwClubs.selectedItem)
        }

        dialogPane.content = borderpane {
            top {
                txtSearch = textfield {
                    promptText = "Zoeken..."
                    textProperty().addListener { _ -> search() }
                }
            }

            center {
                tvwClubs = tableview {
                    column("Vereniging", Club::name) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(1))
                        isResizable = false
                    }

                    onSelectionChange { validate(okButton) }
                    items = (State.meet?.clubs ?: emptyList<Club>()).observable()

                    onDoubleClick {
                        if (validate(okButton)) {
                            result = listOfNotNull(tvwClubs.selectedItem)
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
        val clubs = ArrayList(State.meet?.clubs ?: emptyList())
        if (!txtSearch.text.isNullOrBlank()) {
            clubs.removeIf {
                !it.name.toLowerCase().contains(txtSearch.text.toLowerCase())
            }
        }
        tvwClubs.items = clubs.observable()
    }

    /**
     * Validates input and enables the okButton only if the input is valid.
     *
     * @return `true` when valid, `false` when invalid.
     */
    private fun validate(okButton: Node): Boolean {
        okButton.isDisable = tvwClubs.selectedItem == null
        return !okButton.isDisable
    }
}