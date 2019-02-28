package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.model.SimpleAgeGroup
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.util.fx.icon
import nl.trbres.meetmanager.util.fx.makeEditable
import nl.trbres.meetmanager.util.nestedDelete
import nl.trbres.meetmanager.util.nestedUpdate
import nl.trbres.meetmanager.view.dialog.NewRelayDialog
import nl.trbres.meetmanager.view.dialog.NewSwimmerDialog
import tornadofx.*
import java.util.*

/**
 * @author Ruben Schellekens
 */
open class Contestants(val main: MainView) : BorderPane() {

    lateinit var tvwContestants: TableView<Swimmer>

    init {
        center {
            tvwContestants = tableview {
                useMaxWidth = true

                column("Naam zwemmer", Swimmer::name) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.35))
                    isResizable = false
                }.makeEditable().setOnEditCommit {
                    selectedItem?.name = it.newValue
                    selectedItem?.nestedUpdate()
                }

                column("Vereniging", Swimmer::club) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.3))
                    isResizable = false
                }.makeEditable({ swimmer, club -> swimmer.club = club; swimmer.nestedUpdate() }) {
                    State.meet!!.clubs.toList()
                }

                column("Leeftijdscategorie", Swimmer::age) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.15))
                    isResizable = false
                }.makeEditable({ swimmer, ageGroup -> swimmer.age = ageGroup; swimmer.nestedUpdate() }) {
                    State.meet?.ageSet?.ages?.toList() ?: SimpleAgeGroup.values().toList()
                }

                column("Jaar", Swimmer::birthYear) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                    isResizable = false
                }.makeEditable({ swimmer, birthYear -> swimmer.birthYear = birthYear; swimmer.nestedUpdate(); }) {
                    (1900..Calendar.getInstance().get(Calendar.YEAR)).reversed().toList()
                }

                column("M/V", Swimmer::category) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                    isResizable = false
                }.makeEditable({ swimmer, category -> swimmer.category = category; swimmer.nestedUpdate() }) {
                    listOf(Category.FEMALE, Category.MALE)
                }

                contextmenu {
                    item("Estaffette toevoegen").icon(Icons.add).action(::addRelay)
                    item("Estaffette bewerken").action(::editRelay)
                    item("Zwemmer toevoegen").icon(Icons.add).action(::addSwimmer)
                    separator()
                    item("Verwijderen").icon(Icons.remove).action(::deleteSwimmer)
                }
            }
        }

        populate()
    }

    fun populate() {
        tvwContestants.items.clear()
        val meet = State.meet ?: return
        tvwContestants.items.addAll(meet.swimmers)
    }

    /**
     * Prompts the user for a new swimmer and adds it to the tableview and data model.
     */
    private fun addSwimmer() {
        NewSwimmerDialog(main.currentWindow).showAndWait().ifPresent {
            State.meet?.swimmers?.add(it) ?: return@ifPresent
            tvwContestants.items.add(it)
        }
    }

    /**
     * Prompts the user for a new relay and adds it to the tableview and data model.
     */
    private fun addRelay() {
        val meet = State.meet ?: return
        NewRelayDialog(main.currentWindow).showAndWait().ifPresent {
            val result = it as Relay
            meet.swimmers.add(result)
            tvwContestants.items.add(result)
        }
    }

    /**
     * Shows a dialog that lets the user edit the relay.
     */
    private fun editRelay() {
        val selected = tvwContestants.selectedItem as? Relay ?: return
        NewRelayDialog(main.currentWindow).apply {
            fillInfo(selected)
        }.showAndWait().ifPresent {
            val result = it as Relay
            selected.name = result.name
            selected.club = result.club
            selected.id = result.id
            selected.age = result.age
            selected.members.clear()
            selected.members.addAll(result.members)
            selected.nestedUpdate()
        }
    }

    /**
     * Removes a swimmer from the table view and data model.
     */
    private fun deleteSwimmer() {
        State.meet ?: return
        val selected = tvwContestants.selectedItem ?: return
        tvwContestants.items.remove(selected)
        selected.nestedDelete()
    }

}