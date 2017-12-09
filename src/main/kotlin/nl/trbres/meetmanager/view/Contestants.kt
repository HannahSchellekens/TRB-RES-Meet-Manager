package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.SimpleAgeGroup
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.util.icon
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
                }

                column("Vereniging", Swimmer::club) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.3))
                    isResizable = false
                }

                column("Leeftijdscategorie", Swimmer::age) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.2))
                    isResizable = false
                }

                column("M/V", Swimmer::category) {
                    prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                    isResizable = false
                }

                contextmenu {
                    item("Toevoegen").icon(Icons.add).action { TODO("Add swimmer") }
                    item("Verwijderen").icon(Icons.remove).action { TODO("Remove swmmer") }
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
}