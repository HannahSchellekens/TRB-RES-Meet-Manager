package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.SimpleAgeGroup
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.util.fx.icon
import nl.trbres.meetmanager.util.fx.makeEditable
import nl.trbres.meetmanager.util.nestedDelete
import nl.trbres.meetmanager.util.nestedUpdate
import tornadofx.*

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
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.4))
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
                    SimpleAgeGroup.values().toList()
                }

                column("M/V", Swimmer::category) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.15))
                    isResizable = false
                }.makeEditable({ swimmer, category -> swimmer.category = category; swimmer.nestedUpdate() }) {
                    listOf(Category.FEMALE, Category.MALE)
                }

                contextmenu {
                    item("Toevoegen").icon(Icons.add).action(::addSwimmer)
                    separator()
                    item("Verwijderen").icon(Icons.remove).action(::deleteSwimmer)
                }

                items.add(Swimmer("Ruben Schellekens", SimpleAgeGroup.SENIOREN, Category.MALE, Club("TRB-RES")))
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
     * Removes a swimmer from the table view and data model.
     */
    private fun deleteSwimmer() {
        State.meet ?: return
        val selected = tvwContestants.selectedItem ?: return
        tvwContestants.items.remove(selected)
        selected.nestedDelete()
    }
}