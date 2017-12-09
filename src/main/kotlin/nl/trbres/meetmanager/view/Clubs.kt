package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.util.icon
import tornadofx.*

/**
 * @author Ruben Schellekens
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
                }.makeEditable()

                contextmenu {
                    item("Toevoegen").icon(Icons.add).action { TODO("Add club") }
                    item("Verwijderen").icon(Icons.remove).action { TODO("Remove club") }
                }
            }
        }

        populate()
    }

    fun populate() {
        tvwClubs.items.clear()
        val meet = State.meet ?: return
        tvwClubs.items.addAll(meet.clubs)
    }
}