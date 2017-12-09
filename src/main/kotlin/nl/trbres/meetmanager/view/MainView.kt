package nl.trbres.meetmanager.view

import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.util.APPLICATION_NAME
import nl.trbres.meetmanager.util.APPLICATION_VERSION
import nl.trbres.meetmanager.util.icon
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class MainView() : View() {

    override val root = anchorpane {
        title = "$APPLICATION_NAME ($APPLICATION_VERSION)"
        prefWidth = 800.0
        prefHeight = 600.0
        styleClass += "background"

        menubar {
            fitToParentWidth()

            menu("Bestand") {
                item("Nieuw", "Ctrl+N").icon(Icons.new).action { TODO("New file") }
                item("Opslaan", "Ctrl+S").icon(Icons.save).action { TODO("Save") }
                item("Openen", "Ctrl+O").icon(Icons.folder).action { TODO("Open") }
                separator()
                item("Afsluiten").icon(Icons.exit).action { TODO("Close") }
            }
            menu("Wedstrijd") {
                isDisable = true
            }
        }
    }
}