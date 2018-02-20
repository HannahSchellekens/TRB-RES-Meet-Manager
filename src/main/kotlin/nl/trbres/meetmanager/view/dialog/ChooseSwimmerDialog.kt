package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.model.Swimmer
import tornadofx.*
import java.util.*

/**
 * @author Ruben Schellekens
 */
open class ChooseSwimmerDialog(
        ownerWindow: Window?,
        private val relay: Boolean? = false,
        customMessage: String = "Kies een ${if (relay == false) "zwemmer" else "estaffette"}."
) : Dialog<ChooseSwimmerDialogResult>() {

    private lateinit var txtSearch: TextField
    private lateinit var tvwSwimmers: TableView<Swimmer>

    init {
        title = "Selecteer ${if (relay == false) "zwemmer" else "estaffette"}"
        headerText = customMessage
        dialogPane.minWidth = 500.0
        initOwner(ownerWindow)

        val deleteButton = ButtonType("Baan leeghalen")
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL, deleteButton)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it != ButtonType.OK && it != deleteButton) {
                return@Callback null
            }

            ChooseSwimmerDialogResult(Optional.ofNullable(tvwSwimmers.selectedItem), it == deleteButton)
        }

        dialogPane.content = borderpane {
            top {
                txtSearch = textfield {
                    promptText = "Zoeken..."
                    textProperty().addListener { _ -> search() }
                }
            }

            center {
                tvwSwimmers = tableview {
                    column("Naam ${if (relay == false) "estaffette" else "zwemmer"}", Swimmer::name) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.4))
                        isResizable = false
                    }
                    column("Vereniging", Swimmer::club) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.3))
                        isResizable = false
                    }
                    column("Categorie", Swimmer::age) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.15))
                        isResizable = false
                    }
                    column("M/V", Swimmer::category) {
                        prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.15))
                        isResizable = false
                    }

                    onSelectionChange { validate(okButton) }
                    items = allSwimmers().observable()

                    onDoubleClick {
                        if (validate(okButton)) {
                            result = ChooseSwimmerDialogResult(Optional.ofNullable(tvwSwimmers.selectedItem), false)
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
        val swimmers = ArrayList(allSwimmers())
        if (!txtSearch.text.isNullOrBlank()) {
            swimmers.removeIf {
                val query = txtSearch.text.toLowerCase()
                !it.name.toLowerCase().contains(query) && !(it.club?.name?.toLowerCase()?.contains(query) ?: false)
            }
        }
        tvwSwimmers.items = swimmers.observable()
    }

    /**
     * Validates input and enables the okButton only if the input is valid.
     *
     * @return `true` when valid, `false` when invalid.
     */
    private fun validate(okButton: Node): Boolean {
        okButton.isDisable = tvwSwimmers.selectedItem == null
        return !okButton.isDisable
    }

    /**
     * Picks all selectable swimmers.
     */
    private fun allSwimmers(): List<Swimmer> {
        val meet = State.meet ?: return emptyList()

        return if (relay == null) {
            meet.swimmers.toList()
        }
        else meet.swimmers.filter { (it is Relay) == relay }
    }
}

/**
 * @author Ruben Schellekens
 */
data class ChooseSwimmerDialogResult(val swimmer: Optional<Swimmer>, val removal: Boolean)