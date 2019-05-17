package nl.trbres.meetmanager.view

import javafx.scene.Cursor
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Menu
import javafx.scene.control.TabPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import nl.rubensten.utensils.system.Clipboard
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.UserSettings.Key.lastDirectory
import nl.trbres.meetmanager.export.*
import nl.trbres.meetmanager.import.EventImporter
import nl.trbres.meetmanager.import.SwimmerImporter
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.util.*
import nl.trbres.meetmanager.util.fx.icon
import nl.trbres.meetmanager.util.fx.openEvent
import nl.trbres.meetmanager.view.dialog.*
import tornadofx.*
import java.io.File

/**
 * @author Hannah Schellekens
 */
open class MainView : View() {

    lateinit var tabWrapper: TabPane
    lateinit var viewGeneral: General
    lateinit var viewClubs: Clubs
    lateinit var viewContestants: Contestants
    lateinit var viewSchedule: Schedule
    lateinit var menuMeet: Menu
    lateinit var menuImport: Menu
    lateinit var menuExport: Menu
    lateinit var menuRecent: Menu

    override val root = borderpane {
        prefWidth = 900.0
        prefHeight = 700.0
        styleClass += "background"

        top {
            menubar {
                useMaxWidth = true

                menu("Bestand") {
                    item("Nieuwe wedstrijd", "Ctrl+N").icon(Icons.new).action(::newMeet)
                    item("Openen", "Ctrl+O").icon(Icons.folder).action { loadMeet() }
                    menuRecent = menu("Open recent") {
                        val callback: (List<File>) -> Unit = { files ->
                            items.clear()
                            files.forEach { file ->
                                item(file.absolutePath).action {
                                    loadMeet(file)
                                }
                            }
                        }
                        RecentFiles.callbacks += callback
                        callback(RecentFiles.recentfiles())
                    }
                    item("Opslaan", "Ctrl+S").icon(Icons.save).action(::saveMeet)
                    item("Opslaan als...", "Ctrl+Shift+S").action(::saveAs)
                    separator()
                    item("Afsluiten").icon(Icons.exit).action(::exitProgram)
                }
                menuMeet = menu("Wedstrijd") {
                    item("Zwemmers verdelen").action(::distributeSwimmers)
                    item("Einduitslag genereren").icon(Icons.report).action(::endResults)
                    separator()
                    item("Programmaboekje genereren").icon(Icons.pdf).action(::printBooklet)
                    item("Gepersonaliseerd programma genereren").icon(Icons.pdf).action(::printPersonalisedBooklet)
                    item("Tijdwaarnemingskaartjes genereren").icon(Icons.pdf).action(::printResultCards)
                    item("Data voor certificaten genereren").icon(Icons.textFile).action(::generateCertificates)
                    isDisable = true
                }
                menuImport = menu("Importeren") {
                    item("Programma importeren").icon(Icons.download).action(::importEvents)
                    item("Verenigingen importeren").icon(Icons.download).action(::importClubs)
                    item("Zwemmers importeren").icon(Icons.download).action(::importSwimmers)
                    isDisable = true
                }
                menuExport = menu("Exporteren") {
                    item("Programma exporteren").icon(Icons.upload).action(::exportEvents)
                    item("Vernigingen exporteren").icon(Icons.upload).action(::exportClubs)
                    item("Zwemmers exporteren").icon(Icons.upload).action(::exportSwimmers)
                    isDisable = true
                }
                menu("Opties") {
                    checkmenuitem("PDF bestanden automatisch openen") {
                        isSelected = UserSettings[UserSettings.Key.autoOpenPdfFiles]?.toBoolean() ?: true

                        selectedProperty().addListener { _ ->
                            UserSettings[UserSettings.Key.autoOpenPdfFiles] = isSelected.toString()
                        }
                    }
                }
                menu("Help") {
                    item("Documentatie").icon(Icons.textFile).action { DOCUMENTATION_PAGE.openUrl() }
                    item("GitHub pagina").icon(Icons.link).action { GITHUB_PAGE.openUrl() }
                    item("Over").action(::about)
                }
            }
        }

        center {
            tabWrapper = tabpane {
                tab("Algemeen") {
                    isClosable = false
                    viewGeneral = General(this@MainView)
                    this += viewGeneral
                    openEvent { viewGeneral.populate() }
                }
                tab("Verenigingen") {
                    isClosable = false
                    viewClubs = Clubs(this@MainView)
                    this += viewClubs
                    openEvent { viewClubs.populate() }
                }
                tab("Deelnemers") {
                    isClosable = false
                    viewContestants = Contestants(this@MainView)
                    this += viewContestants
                    openEvent { viewContestants.populate() }
                }
                tab("Programma") {
                    isClosable = false
                    viewSchedule = Schedule(this@MainView)
                    this += viewSchedule
                    openEvent { viewSchedule.updateProgram() }
                }
            }
        }

        updateFromState()
    }

    init {
        currentStage?.setOnCloseRequest { safeClose() }
    }

    /**
     * Generates a booklet containing all events/heats.
     */
    fun printBooklet() {
        State.meet ?: return
        BookletPrinter.printBooklet(output = null, owner = currentWindow, highlight = null)
    }

    /**
     * Generates a booklet where one club is highlighted.
     */
    fun printPersonalisedBooklet() {
        State.meet ?: return

        val dialog = ChooseClubDialog(currentWindow, allowAllClubs = true)
        val clubs = dialog.showAndWait().getOrNull() ?: return
        when (clubs.size) {
            1 -> BookletPrinter.printBooklet(output = null, owner = currentWindow, highlight = clubs.firstOrNull())
            else -> {
                val directory = DirectoryChooser().apply {
                    title = "Kies een map om de programmaboekjes in op te slaan..."
                    UserSettings[UserSettings.Key.lastScheduleDirectory].whenNonNull {
                        try {
                            initialDirectory = it.file()
                        }
                        catch (ignored: Exception) { }
                    }
                }.showDialog(currentWindow) ?: return

                clubs.forEach {
                    val name = fileNameOfSchedule(State.meet!!, it)
                    val output = File("${directory.absolutePath}/$name")
                    BookletPrinter.printBooklet(output = output, owner = currentWindow, highlight = it, openOnFinish = false)
                }

                Alert(Alert.AlertType.INFORMATION, "${clubs.size} programma's zijn geëxporteerd naar de map $directory", ButtonType.OK).apply {
                    title = "Programmaboekjes"
                    headerText = "Opslaan gelukt!"
                    initOwner(currentWindow)
                }.showAndWait()
            }
        }
    }

    /**
     * Generates a PDF of all result cards.
     */
    fun printResultCards() {
        State.meet ?: return
        ResultCardPrinter.printCards(currentWindow)
    }

    /**
     * Show a prompt asking the user for a club.
     */
    private fun promptClub(): Club? {
        TODO("Implement")
    }

    /**
     * Distributes all swimmers over the matching events.
     */
    fun distributeSwimmers() {
        val meet = State.meet ?: return
        meet.distributor.distribute()
        updateFromState()

        information("De zwemmers zijn verdeeld!", title = "Succes!", owner = currentWindow)
    }

    /**
     * Generate the end results.
     */
    fun endResults() {
        val meet = State.meet ?: return
        val dialog = EndResultDialog(currentWindow)
        dialog.showAndWait().ifPresent { result ->
            val text = result.events
            val numbers = text.replace(" ", "")
                    .split(",")
                    .filter { it.isNaturalNumber() }
                    .map { it.toInt() }
                    .filter { it - 1 < meet.events.size }
            if (numbers.isEmpty()) {
                return@ifPresent
            }

            val events = numbers.map { meet.events[it - 1] }
            EndResultPrinter.printResults(events, numbers, result.filter, result.convertTo, currentWindow)
        }
    }

    /**
     * Generate the certificate data merge file.
     */
    fun generateCertificates() {
        val meet = State.meet ?: return
        val dialog = EndResultDialog(currentWindow)
        dialog.showAndWait().ifPresent { result ->
            val text = result.events
            val numbers = text.replace(" ", "")
                    .split(",")
                    .filter { it.isNaturalNumber() }
                    .map { it.toInt() }
                    .filter { it - 1 < meet.events.size }
            if (numbers.isEmpty()) {
                return@ifPresent
            }

            val events = numbers.map { meet.events[it - 1] }
            CertificateExport.exportTextFile(CertificateExportMeta(events, numbers, result.filter, result.convertTo), currentWindow)
        }
    }

    /**
     * Imports events from a given (user inputted) list.
     */
    fun importEvents() {
        val meet = State.meet ?: return
        ImportEventsDialog(currentWindow).showAndWait().ifPresent {
            val newEvents = EventImporter(it, meet).import()
            meet.events.addAll(newEvents)
            updateFromState()
            information("${newEvents.size} programma's zijn toegevoegd!", owner = currentWindow, title = "Succes!")
        }
    }

    /**
     * Imports swimmers from a given (user inputted) list.
     */
    fun importSwimmers() {
        val meet = State.meet ?: return
        ImportSwimmersDialog(currentWindow).showAndWait().ifPresent {
            val newSwimmers = SwimmerImporter(it, meet).import()
            meet.swimmers.addAll(newSwimmers)
            updateFromState()
            information("${newSwimmers.size} zwemmers zijn toegevoegd!", owner = currentWindow, title = "Succes!")
        }
    }

    /**
     * Imports clubs from a given (user inputted) list.
     */
    fun importClubs() {
        val meet = State.meet ?: return
        ImportClubsDialog(currentWindow).showAndWait().ifPresent {
            val clubs = meet.clubs.asSequence().map { club -> club.name }.toHashSet()
            var added = 0
            it.forEach { clubName ->
                if (clubName in clubs) return@forEach
                meet.clubs += Club(clubName)
                added++
            }
            updateFromState()
            information("$added verenigingen zijn toegevoegd!", owner = currentWindow, title = "Succes!")
        }
    }

    /**
     * Exports the swimmers to the clipboard.
     */
    fun exportSwimmers() {
        val meet = State.meet ?: return
        val export = meet.swimmers.filter { it !is Relay }.joinToString("\n") {
            buildString {
                append(if (it.category == Category.MALE) "m" else "v")
                append("\t")
                append(it.name)
                append("\t")
                append(it.age.id)
                append("\t")
                append(it.club?.name)
                append("\t")
                append(it.birthYear ?: "")
            }
        }
        Clipboard.set(export)
        information("De zwemmers zijn gekopiëerd naar het klembord.", owner = currentWindow, title = "Zwemmers exporteren")
    }

    /**
     * Exports the clubs to the clipboard.
     */
    fun exportClubs() {
        val meet = State.meet ?: return
        val export = meet.clubs.joinToString("\n") { it.name }
        Clipboard.set(export)
        information("De verenigingen zijn gekopiëerd naar het klembord.", owner = currentWindow, title = "Verenigingen exporteren")
    }

    /**
     * Exports all the events to the clipboard.
     */
    fun exportEvents() {
        val meet = State.meet ?: return
        val export = meet.events.joinToString("\n") {
            buildString {
                append(it.distance.times).append("x").append(it.distance.metres)
                append("\t")
                append(it.stroke.name)
                append("\t")
                append(it.ages.first().id)
                append("\t")
                append(when (it.category) {
                    Category.MALE -> "m"
                    Category.FEMALE -> "v"
                    else -> "x"
                })
            }
        }
        Clipboard.set(export)
        information("Het programma is gekopiëerd naar het klembord.", owner = currentWindow, title = "Programma exporteren")
    }

    /**
     * Updates all UI elements to line up with the global meet state.
     */
    fun updateFromState() {
        // (un)lock controls
        tabWrapper.isVisible = State.meet != null
        menuImport.isDisable = State.meet == null
        menuExport.isDisable = State.meet == null
        menuMeet.isDisable = State.meet == null

        // Update contents.
        updateTitle()
        viewGeneral.populate()
        viewClubs.populate()
        viewContestants.populate()
        viewSchedule.updateProgram()
    }

    /**
     * Updates the title of the window.
     */
    fun updateTitle() {
        title = "$APPLICATION_NAME ($APPLICATION_VERSION) - " + if (State.meet == null) "<Geen wedstrijd>" else State.meet?.name
    }

    /**
     * Prompts to save before closing.
     */
    private fun safeClose() {
        State.meet ?: return
        confirm("Wil je onopgeslagen wijzigingen opslaan?",
                confirmButton = ButtonType.YES, cancelButton = ButtonType.NO, owner = currentWindow) {
            saveMeet()
        }
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
        State.saveFile = null
        updateFromState()
    }

    /**
     * Saves the currently active swim meet to the lastly saved to/opened file.
     */
    private fun saveMeet() {
        val file = State.saveFile ?: return saveAs()

        val meet = State.meet ?: kotlin.run {
            warning("Er is geen wedstrijd om op te slaan!", owner = currentStage, title = "Geen westrijd")
            return
        }

        fakeLoadCursor()
        usefulTry("Kon het wedstrijd bestand niet opslaan naar ${file.name}") { meet serializeTo file }
    }

    /**
     * Saves the currently active swim meet, or prompts when there is no meet active.
     */
    private fun saveAs() {
        val meet = State.meet ?: kotlin.run {
            warning("Er is geen wedstrijd om op te slaan!", owner = currentStage, title = "Geen westrijd")
            return
        }

        val file = FileChooser().apply {
            title = "Wedstrijdbestand opslaan..."
            extensionFilters += FileChooser.ExtensionFilter("Wedstrijden", "*.meet")
            initialFileName = meet.fileNameFriendlyString() + ".meet"
            UserSettings[lastDirectory].whenNonNull {
                try {
                    initialDirectory = it.file()
                }
                catch (ignored: Exception) {}
            }
        }.showSaveDialog(currentStage) ?: return
        State.saveFile = file
        RecentFiles.pushRecentFile(file)
        UserSettings[lastDirectory] = file.parentFile.absolutePath

        usefulTry("Kon het wedstrijdbestand niet opslaan naar ${file.name}") { meet serializeTo file }
    }

    /**
     * Loads a swim meet from a file to the program.
     *
     * @param meetFile
     *          The file to open, or `null` to show a prompt first.
     */
    private fun loadMeet(meetFile: File? = null) {
        val file = meetFile ?: FileChooser().apply {
            title = "Wedstrijdbestand openen..."
            extensionFilters += FileChooser.ExtensionFilter("Wedstrijden", "*.meet")
            UserSettings[lastDirectory].whenNonNull {
                try {
                    initialDirectory = it.file()
                }
                catch (ignored: Exception) { }
            }
        }.showOpenDialog(currentWindow) ?: return
        UserSettings[lastDirectory] = file.parentFile.absolutePath

        if (file.exists().not()) {
            RecentFiles.removeFile(file)
            Alert(Alert.AlertType.ERROR, file.absolutePath, ButtonType.OK).apply {
                title = "Fout"
                headerText = "Het opgegeven wedstrijdbestand bestaat niet!"
                initOwner(currentWindow)
            }.showAndWait()
            return
        }

        State.meet = usefulTry("Kon het wedstrijdbestand niet inladen") { file.deserialize() }
        State.saveFile = file
        RecentFiles.pushRecentFile(file)
        updateFromState()
    }

    /**
     * Exits with exit code 0.
     */
    private fun exitProgram() {
        safeClose()
        currentStage?.close() ?: System.exit(1)
    }

    /**
     * Shows a dialog with general info about the software.
     */
    private fun about() {
        val buttonWebsite = ButtonType("Website bezoeken")
        val buttonLicense = ButtonType("Licentie")

        val alert = Alert(
                Alert.AlertType.INFORMATION,
                "$APPLICATION_NAME, versie $APPLICATION_VERSION\n ",
                ButtonType.CLOSE,
                buttonWebsite,
                buttonLicense
        ).apply {
            title = "Over"
            headerText = "Gemaakt door Hannah Schellekens\nhttps://hannahschellekens.nl\nGepubliceerd onder de GPLv3 licentie"
            initOwner(currentWindow)
        }

        when (alert.showAndWait().orElse(null)) {
            buttonWebsite -> AUTHOR_HOME.openUrl()
            buttonLicense -> LICENSE.openUrl()
        }
    }

    /**
     * Turn the cursor in a loading cursor for a short while for visual feedback.
     */
    private fun fakeLoadCursor() {
        currentStage?.scene?.cursor = Cursor.WAIT
        runAsync {
            Thread.sleep(350)
            runLater {
                currentStage?.scene?.cursor = Cursor.DEFAULT
            }
        }
    }
}