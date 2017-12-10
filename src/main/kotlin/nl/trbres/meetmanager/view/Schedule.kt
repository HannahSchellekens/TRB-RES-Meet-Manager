package nl.trbres.meetmanager.view

import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.State
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

                            for ((heatNo, _) in event.heats.withIndex()) {
                                fold("Serie ${heatNo + 1}") {
                                    styleClass += "heat"
                                    stackpane {
                                        label("Nothing here")
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