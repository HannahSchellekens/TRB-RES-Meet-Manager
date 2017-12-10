package nl.trbres.meetmanager.view

import javafx.collections.ObservableList
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Heat
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.time.Time
import nl.trbres.meetmanager.time.TimeConverter
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class Schedule(val main: MainView) : BorderPane() {

    init {
        updateProgram()
    }

    fun updateProgram() {
        // Fetch state data
        val meet = State.meet ?: return
        val events = meet.events

        clear()

        // Add all events
        center {
            squeezebox {
                multiselect = false

                for ((eventNo, event) in events.withIndex()) {
                    fold("Programma ${eventNo + 1}: $event") {
                        styleClass += "event"

                        // Add all heats
                        squeezebox {
                            multiselect = false

                            for ((heatNo, heat) in event.heats.withIndex()) {
                                fold("Serie ${heatNo + 1}") {
                                    styleClass += "heat"

                                    tableview<ScheduleEntry> {
                                        // Lane
                                        column("Baan", ScheduleEntry::lane) {
                                            isSortable = false
                                        }

                                        // Swimmer
                                        column("Zwemmer", ScheduleEntry::name) {
                                            prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.3))
                                            isSortable = false
                                        }

                                        // Club
                                        column("Vereniging", ScheduleEntry::club) {
                                            prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.2))
                                            isSortable = false
                                        }

                                        // Result
                                        column("Tijd", ScheduleEntry::time) {
                                            prefWidthProperty().bind(this@tableview.widthProperty().multiply(0.1))
                                            isSortable = false
                                        }.makeEditable(TimeConverter()).setOnEditCommit {
                                            updateTime(it.newValue, selectedItem ?: return@setOnEditCommit)
                                        }

                                        items = heat.toScheduleEntries()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the time of the given entry.
     */
    private fun updateTime(newTime: Time, entry: ScheduleEntry) {
        if (entry.name == "<Leeg>") {
            return
        }
        entry.heat.results[entry.lane] = newTime
    }

    /**
     * Turns everything in the heat into [ScheduleEntry]'s so they can be represented in a table view.
     */
    private fun Heat.toScheduleEntries(): ObservableList<ScheduleEntry> {
        val list = ArrayList<ScheduleEntry>()

        for (lane in State.meet!!.lanes) {
            val swimmer = lanes[lane]
            val time = results[lane] ?: Time(0, 0)
            list += ScheduleEntry(
                    lane, swimmer?.name ?: "<Leeg>", swimmer?.club?.name ?: "<Geen>", time, swimmer, this
            )
        }

        return list.observable()
    }
}

/**
 * @author Ruben Schellekens
 */
data class ScheduleEntry(var lane: Int, var name: String, var club: String, var time: Time, var swimmer: Swimmer?, var heat: Heat)