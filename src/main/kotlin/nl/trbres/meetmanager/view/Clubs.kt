package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.util.fx.icon
import nl.trbres.meetmanager.util.nestedDelete
import nl.trbres.meetmanager.util.nestedUpdate
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class Clubs(val main: MainView) : BorderPane() {

    lateinit var tvwClubs: TableView<Club>

    init {
        center {
            tvwClubs = tableview {
                useMaxWidth = true
                isEditable = true
                column("Naam vereniging", Club::name) {
                    prefWidthProperty().bind(this@tableview.widthProperty())
                    isResizable = false
                }.makeEditable().setOnEditCommit {
                    selectedItem?.name = it.newValue
                    selectedItem?.nestedUpdate()
                }

                contextmenu {
                    item("Toevoegen").icon(Icons.add).action(::addClub)
                    item("Verwijderen").icon(Icons.remove).action(::deleteClub)
                }
            }
        }

        populate()
    }

    /**
     * Puts all the available clubs in the table view.
     */
    fun populate() {
        tvwClubs.items.clear()
        val meet = State.meet ?: return
        tvwClubs.items.addAll(meet.clubs)
    }

    /**
     * Prompts the user for a new club and adds it to the tableview and data model.
     */
    private fun addClub() {
        val input = TextInputDialog().apply {
            title = "Nieuwe vereniging"
            headerText = "Geef de naam op voor een nieuwe vereniging"
            initOwner(main.currentWindow)
        }

        input.showAndWait().ifPresent {
            val created = Club(it)
            State.meet?.clubs?.add(created)
            tvwClubs.items.add(created)
        }
    }

    /**
     * Deletes a club (duh) and updates the tableview and data model.
     */
    private fun deleteClub() {
        State.meet ?: return
        val selected = tvwClubs.selectedItem ?: return
        tvwClubs.items.remove(selected)
        selected.nestedDelete()
    }
}