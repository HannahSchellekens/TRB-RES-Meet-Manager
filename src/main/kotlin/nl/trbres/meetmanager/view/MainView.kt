package nl.trbres.meetmanager.view

import javafx.scene.control.ButtonType
import javafx.stage.FileChooser
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.util.*
import tornadofx.*
import kotlin.system.exitProcess

/**
 * @author Ruben Schellekens
 */
open class MainView() : View() {

    lateinit var viewClubs: Clubs
    lateinit var viewContestants: Contestants
    lateinit var viewSchedule: Schedule

    override val root = borderpane {
        updateTitle()
        prefWidth = 800.0
        prefHeight = 600.0
        styleClass += "background"

        top {
            menubar {
                useMaxWidth = true

                menu("Bestand") {
                    item("Nieuwe wedstrijd", "Ctrl+N").icon(Icons.new).action(::newMeet)
                    item("Opslaan", "Ctrl+S").icon(Icons.save).action(::saveMeet)
                    item("Openen", "Ctrl+O").icon(Icons.folder).action(::loadMeet)
                    separator()
                    item("Afsluiten").icon(Icons.exit).action(::exitProgram)
                }
                menu("Wedstrijd") {
                    item("Maak programma").icon(Icons.report).action { TODO("Create schedule") }
                    item("Exporteer programma naar PDF").icon(Icons.pdf).action { TODO("Export schedule to PDF") }
                }
                menu("Help") {
                    item("Documentatie").icon(Icons.textFile).action { TODO("Add link to docs") }
                    item("GitHub pagina").icon(Icons.link).action { TODO("Add link to github") }
                    item("Over").action { TODO("Add prompt with program info") }
                }
            }
        }

        center {
            tabpane {
                tab("Verenigingen") {
                    isClosable = false
                    viewClubs = Clubs(this@MainView)
                    this += viewClubs
                }
                tab("Deelnemers") {
                    isClosable = false
                    viewContestants = Contestants(this@MainView)
                    this += viewContestants
                }
                tab("Programma") {
                    isClosable = false
                    viewSchedule = Schedule(this@MainView)
                    this += viewSchedule
                }
            }
        }
    }

    /**
     * Updates the title of the window.
     */
    private fun updateTitle() {
        title = "$APPLICATION_NAME ($APPLICATION_VERSION)" + if (State.meet == null) "" else " - ${State.meet?.name}"
    }

    /**
     * Creates a new meet.
     */
    private fun newMeet() {
        // Prompt for unsaved changes.
        if (State.meet != null) {
            var `continue` = false
            confirm("Weet je zeker dat je een nieuwe wedstrijd aan wilt maken?",
                    "Onopgeslagen wijzigingen zullen verloren gaan.",
                    confirmButton = ButtonType.YES, cancelButton = ButtonType.NO, owner = currentWindow) {
                `continue` = true
            }

            if (!`continue`) {
                return
            }
        }

        // Create new meet.
        val meetReference = Reference<Meet>(null)
        find<NewMeetDialog>(mapOf("meet" to meetReference)).openModal(block = true, resizable = false, owner = currentWindow)
        State.meet = meetReference.value ?: return
        updateFromState()
    }

    /**
     * Saves the currently active swim meet, or prompts when there is no meet active.
     */
    private fun saveMeet() {
        val meet = State.meet ?: kotlin.run {
            warning("Er is geen wedstrijd om op te slaan!", owner = currentStage, title = "Geen westrijd")
            return
        }

        val file = FileChooser().apply {
            title = "Wedstrijdbestand opslaan..."
            extensionFilters += FileChooser.ExtensionFilter("Wedstrijden", "*.meet")
        }.showSaveDialog(currentStage) ?: return

        usefulTry("Kon het wedstrijdbestand niet opslaan") { meet serializeTo file }
    }

    /**
     * Loads a swim meet from a file to the program.
     */
    private fun loadMeet() {
        val file = FileChooser().apply {
            title = "Wedstrijdbestand openen..."
            extensionFilters += FileChooser.ExtensionFilter("Wedstrijden", "*.meet")
        }.showOpenDialog(currentWindow) ?: return

        State.meet = usefulTry("Kon het wedstrijdbestand niet inladen") { file.deserialize() }
        updateFromState()
    }

    /**
     * Updates all UI elements to line up with the global meet state.
     */
    private fun updateFromState() {
        updateTitle()
        viewClubs.populate()
        viewContestants.populate()
        viewSchedule.updateProgram()
    }

    /**
     * Exits with exit code 0.
     */
    private fun exitProgram(): Nothing = exitProcess(0)
}