package nl.trbres.meetmanager.view

import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TitledPane
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.export.EventResultPrinter
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.model.Heat
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.time.Time
import nl.trbres.meetmanager.time.TimeConverter
import nl.trbres.meetmanager.util.fx.icon
import nl.trbres.meetmanager.util.fx.styleClass
import nl.trbres.meetmanager.util.indexRange
import nl.trbres.meetmanager.view.dialog.ChooseSwimmerDialog
import nl.trbres.meetmanager.view.dialog.EventDialog
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class Schedule(val main: MainView) : BorderPane() {

    private lateinit var swimEvents: SqueezeBox
    private var selectedEvent: Event? = null
    private var selectedEventNo: Int? = null
    private var selectedHeat: Heat? = null
    private var selectedHeatNo: Int? = null
    private var eventPanes = ArrayList<TitledPane>()
    private var heatPanes = ArrayList<TitledPane>()

    init {
        updateProgram()
    }

    fun updateProgram() {
        // Fetch state data
        val meet = State.meet ?: return
        val events = meet.events

        eventPanes.clear()
        heatPanes.clear()

        clear()

        // Add all events
        center {
            swimEvents = squeezebox {
                multiselect = false

                for ((eventNo, event) in events.withIndex()) {
                    fold("Programma ${eventNo + 1}: $event") {
                        eventPanes.add(this)
                        styleClass += "event"

                        // Add all heats
                        squeezebox {
                            multiselect = false

                            for ((heatNo, heat) in event.heats.withIndex()) {
                                fold("Serie ${heatNo + 1}") heatFold@ {
                                    styleClass += "heat"
                                    heatPanes.add(this@heatFold)

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

                                    focusedProperty().addListener { _ ->
                                        selectedHeat = heat
                                        selectedHeatNo = heatNo + 1
                                    }
                                }
                            }
                        }

                        focusedProperty().addListener { _ ->
                            selectedEvent = event
                            selectedEventNo = eventNo + 1
                        }
                    }
                }
            }
        }

        right {
            vbox {
                spacing = 2.0
                paddingTop = 2.0
                paddingLeft = 2.0
                paddingRight = 2.0

                label("Prog.") {
                    alignment = Pos.CENTER
                    prefHeight = 25.0
                }

                button {
                    icon(Icons.print)
                    styleClass("cornflower")
                    tooltip("Uitslag printen")
                    action(::eventPrintResults)
                }
                button {
                    icon(Icons.up)
                    styleClass("cornflower")
                    tooltip("Programma naar boven verplaatsen")
                    action(::eventMoveUp)
                }
                button {
                    icon(Icons.down)
                    styleClass("cornflower")
                    tooltip("Programma naar beneden verplaatsen")
                    action(::eventMoveDown)
                }
                button {
                    icon(Icons.add)
                    styleClass("cornflower")
                    tooltip("Programma toevoegen")
                    action(::addEvent)
                }
                button {
                    icon(Icons.textFile)
                    styleClass("cornflower")
                    tooltip("Programma bewerken")
                    action(::editEvent)
                }
                button {
                    icon(Icons.remove)
                    styleClass("cornflower")
                    tooltip("Geselecteerde programma verwijderen")
                    action(::removeEvent)
                }

                label("Series") {
                    alignment = Pos.CENTER
                    prefHeight = 25.0
                }

                button {
                    icon(Icons.up)
                    styleClass("cornflower")
                    tooltip("Serie naar boven verplaatsen")
                    action(::heatMoveUp)
                }
                button {
                    icon(Icons.down)
                    styleClass("cornflower")
                    tooltip("Serie naar beneden verplaatsen")
                    action(::heatMoveDown)
                }
                button {
                    icon(Icons.add)
                    styleClass("cornflower")
                    tooltip("Serie toevoegen")
                    action(::addHeat)
                }
                button {
                    icon(Icons.remove)
                    styleClass("cornflower")
                    tooltip("Serie verwijderen")
                    action(::removeHeat)
                }
            }
        }
    }

    /**
     * Print the results of the selected event.
     */
    private fun eventPrintResults() {
        val event = selectedEvent ?: return
        val number = selectedEventNo ?: 0
        EventResultPrinter.printResults(event, number, main.currentWindow)
    }

    /**
     * Expands the currently selected event and heat.
     */
    private fun expandCurrent(heatDiff: Int = 0, eventDiff: Int = 0) {
        runLater {
            val eventNumber = selectedEventNo ?: return@runLater
            val index = eventNumber - 1 + eventDiff
            if (index in eventPanes.indexRange()) {
                eventPanes[index].isExpanded = true
            }
        }

        runLater {
            val heatNumber = selectedHeatNo ?: return@runLater
            val index = heatNumber - 2 + heatDiff
            if (index in heatPanes.indexRange()) {
                heatPanes[index].isExpanded = true
            }
        }
    }

    /**
     * Move the selected heat 1 place up.
     */
    private fun heatMoveUp() {
        val event = selectedEvent ?: return
        val heat = selectedHeat ?: return
        val number = selectedHeatNo ?: return
        if (number <= 1) {
            return
        }

        val above = event.heats[number - 2]
        event.heats[number - 2] = heat
        event.heats[number - 1] = above

        updateProgram()
        expandCurrent()
    }

    /**
     * Move the selected heat 1 place down.
     */
    private fun heatMoveDown() {
        val event = selectedEvent ?: return
        val heat = selectedHeat ?: return
        val number = selectedHeatNo ?: return
        if (number >= event.heats.size) {
            return
        }

        val below = event.heats[number]
        event.heats[number] = heat
        event.heats[number - 1] = below

        updateProgram()
        expandCurrent()
    }

    /**
     * Adds an empty heat to the currently selected event.
     */
    private fun addHeat() {
        selectedEvent?.heats?.add(Heat())
        updateProgram()
        expandCurrent()
    }

    /**
     * Removes the selected heat.
     */
    private fun removeHeat() {
        val heat = selectedHeat ?: return
        confirm("Weet je zeker dat je programma $selectedEventNo, serie $selectedHeatNo wilt verwijderen?",
                owner = main.currentWindow) {
            selectedEvent?.heats?.remove(heat)
            updateProgram()
            if (selectedEvent?.heats?.isNotEmpty() == true) {
                expandCurrent()
            }
        }
    }

    /**
     * Move the selected event 1 place up.
     */
    private fun eventMoveUp() {
        val meet = State.meet ?: return
        val event = selectedEvent ?: return
        val number = selectedEventNo ?: return
        if (number <= 1) {
            return
        }

        val above = meet.events[number - 2]
        meet.events[number - 2] = event
        meet.events[number - 1] = above

        updateProgram()
    }

    /**
     * Move the selected event 1 place down.
     */
    private fun eventMoveDown() {
        val meet = State.meet ?: return
        val event = selectedEvent ?: return
        val number = selectedEventNo ?: return
        if (number >= meet.events.size) {
            return
        }

        val below = meet.events[number]
        meet.events[number] = event
        meet.events[number - 1] = below

        updateProgram()
    }

    /**
     * Adds a new event to the schedule.
     */
    private fun addEvent() {
        EventDialog(main.currentWindow).showAndWait().ifPresent {
            State.meet?.events?.add(it)
            updateProgram()
        }
    }

    /**
     * Edits the selected event, does nothing when no event is selected.
     */
    private fun editEvent() {
        val event = selectedEvent ?: return
        val eventNumber = selectedEventNo ?: return
        EventDialog(main.currentWindow, event).showAndWait().ifPresent {
            State.meet?.events!![eventNumber - 1] = it
            updateProgram()
        }
    }

    /**
     * Removes the selected event from the schedule.
     */
    private fun removeEvent() {
        val event = selectedEvent ?: return
        confirm(event.toString(),
                "Weet je zeker dat je programma $selectedEventNo wilt verwijderen?",
                owner = main.currentWindow) {
            State.meet?.events?.remove(event)
            updateProgram()
            selectedEvent = null
            selectedEventNo = null
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
                relay = selectedEvent?.isRelay(),
                customMessage = "Kies een ${if (selectedEvent?.isRelay() == false) "zwemmer" else "estaffette"} voor Programma $eventNumber, Serie $heatNumber, Baan ${current.lane}"
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
        entry.time = newTime
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