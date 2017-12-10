package nl.trbres.meetmanager.view

import javafx.scene.control.TableView
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

                                        items.addAll(heat.toScheduleEntries())

                                        onDoubleClick {
                                            replaceSwimmer(eventNo + 1, heatNo + 1)
                                        }
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
     * Asks the user for a swimmer to replace the selected swimmer with.
     */
    private fun TableView<ScheduleEntry>.replaceSwimmer(eventNumber: Int, heatNumber: Int) {
        val current = selectedItem ?: return
        val index = items.indexOf(current)
        val dialog = ChooseSwimmerDialog(
                main.currentWindow,
                "Kies een zwemmer voor Programma $eventNumber, Serie $heatNumber, Baan ${current.lane}"
        )

        dialog.showAndWait().ifPresent {
            val swimmer = it.swimmer.orElse(null)
            val doRemove = it.removal

            // Remove swimmer
            if (doRemove) {
                current.heat.apply {
                    lanes.remove(current.lane)
                    results.remove(current.lane)
                    statusses.remove(current.lane)
                }
                items[index] = ScheduleEntry(current.lane, "<Leeg>", "", Time(0, 0), null, current.heat)
                return@ifPresent
            }

            // On removal, swimmer is `null`.
            swimmer ?: return@ifPresent

            // Change swimmer.
            if (swimmer in current.heat.lanes.values) {
                warning("$swimmer ligt al in de serie!", owner = main.currentWindow, title = "Oeps")
                return@ifPresent
            }

            current.heat.apply {
                lanes[current.lane] = swimmer
                results.remove(current.lane)
                statusses.remove(current.lane)
            }
            items[index] = ScheduleEntry(current.lane, swimmer.name, swimmer.club?.name ?: "", Time(0, 0), swimmer, current.heat)
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
    private fun Heat.toScheduleEntries(): List<ScheduleEntry> {
        val list = ArrayList<ScheduleEntry>()

        for (lane in State.meet!!.lanes) {
            val swimmer = lanes[lane]
            val time = results[lane] ?: Time(0, 0)
            list += ScheduleEntry(
                    lane, swimmer?.name ?: "<Leeg>", swimmer?.club?.name ?: "", time, swimmer, this
            )
        }

        return list
    }
}

/**
 * @author Ruben Schellekens
 */
data class ScheduleEntry(var lane: Int, var name: String, var club: String, var time: Time, var swimmer: Swimmer?, var heat: Heat)