package nl.trbres.meetmanager.export

import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.filter.AgeGroupFilter
import nl.trbres.meetmanager.filter.SwimmerFilter
import nl.trbres.meetmanager.model.*
import nl.trbres.meetmanager.util.file
import nl.trbres.meetmanager.util.whenNonNull
import java.io.File

/**
 * @author Ruben Schellekens
 */
object CertificateExport {

    /**
     * Exports all meet data to be used for data merge in tab separated `.txt` format.
     */
    @JvmStatic
    fun exportTextFile(meta: CertificateExportMeta, owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val swimmers = meet.swimmers
        val txtFile = promptSaveLocation(meta, owner) ?: return

        // Generate data
        val header = "GROUP\tCATEGORY\tYEAR\tNAME\tSCHOOL\tDATE\tLOCATION\tTIME_BACK\tPLACE_BACK\tTIME_BREAST\tPLACE_BREAST\tTIME_FREE\tPLACE_FREE\tTIME_TOTAL\tPLACE_TOTAL\tRELAY_MESSAGE\tRELAY_TEAM\tRELAY_EVENT\tRELAY_TIME\tRELAY_PLACE\n"
        val body = buildString {
            for (swimmer in swimmers) {
                processSwimmer(swimmer, meta, meet)
            }
        }

        txtFile.writeText(header + body)
    }

    /**
     * Creates a line of data from a single swimmer ending with a newline.
     */
    private fun StringBuilder.processSwimmer(swimmer: Swimmer, meta: CertificateExportMeta, meet: Meet) {
        val results = meta.results[swimmer] ?: return

        append(swimmer.group()); tab()
        append(swimmer.category()); tab()
        append(meet.date.year); tab()
        append(swimmer.name); tab()
        append(swimmer.club?.name); tab()
        append(meet.date.toDutchName()); tab()
        append(meet.location); tab()

        append(results["25m Rugslag"]!!.first); tab(); append(results["25m Rugslag"]!!.second); tab()
        append(results["25m Schoolslag"]!!.first); tab(); append(results["25m Schoolslag"]!!.second); tab()
        append(results["25m Vrije slag"]!!.first); tab(); append(results["25m Vrije slag"]!!.second); tab()
        append(results["total"]!!.first); tab(); append(results["total"]!!.second); tab()

        val relay = meta.relay[swimmer]
        append(if (relay == null) "" else "en heeft gezwommen in estafetteteam"); tab()
        append(relay?.get(1) ?: ""); tab()
        append(relay?.get(0) ?: ""); tab()
        append(relay?.get(2) ?: ""); tab()
        append(relay?.get(3) ?: "")

        append('\n')
    }

    private fun Swimmer.category() = if (age.readableName.contains("Wz")) {
        "wedstrijdzwemmers"
    }
    else "recreanten"

    private fun Swimmer.group(): String {
        val numberMatcher = Regex("\\d+")
        return numberMatcher.find(age.readableName)?.value ?: "?"
    }

    private fun StringBuilder.tab() = append('\t')

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(meta: CertificateExportMeta, owner: Window?): File? {
        val fileNameSuffix = if (meta.swimmerFilter is AgeGroupFilter) {
            "_${meta.swimmerFilter.group.readableName.toLowerCase().replace(" ", "-")}"
        }
        else ""

        val meetName = State.meet!!.name
                .replace(" ", "")
                .replace(Regex("[^A-Za-z()\\-0-9&]"), "-")

        val result = FileChooser().apply {
            title = "Data mergebestand opslaan..."
            initialFileName = "MeetData_${meetName}_$fileNameSuffix.txt"
            extensionFilters += FileChooser.ExtensionFilter("TXT Bestanden", "*.txt")
            UserSettings[UserSettings.Key.dataMergeFile].whenNonNull {
                try {
                    initialDirectory = it.file()
                }
                catch (ignored: Exception) { }
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.dataMergeFile] = result.parent
        return result
    }
}

/**
 * Data required for the export that is not readily available in the meet data.
 *
 * @author Ruben Schellekens
 */
class CertificateExportMeta(
        val events: List<Event>,
        val eventNumbers: List<Int>,
        val swimmerFilter: SwimmerFilter = SwimmerFilter.NO_FILTER,
        val convertTo: Int?
) {

    /**
     * `swimmer -> (event name -> (time, place))`
     */
    val results = HashMap<Swimmer, MutableMap<String, Pair<String, String>>>()

    /**
     * `event -> (place -> #wzAbove)`
     */
    val wedstrijdzwemmersAbove = HashMap<Event, List<Int>>()

    /**
     * `swimmer -> (event name, relay name, time, place)`
     */
    val relay = HashMap<Swimmer, List<String>>()

    init {
        val meet = State.meet ?: error("No meet selected.")

        initialiseIndividual(meet)
        initialiseRelay(meet)
    }

    private fun initialiseRelay(meet: Meet) = meet.events.forEach { event ->
        if (!event.isRelay()) return@forEach

        val eventName = "${event.distance} ${event.stroke}".toLowerCase()
        val results = event.swimResults(convertTo)

        results.forEachIndexed { index, result->
            val relay = result.swimmer as Relay
            val time = result.result

            for (member in relay.members) {
                this.relay[member] = listOf(eventName, relay.name, time.toString(), "${index + 1}e plaats")
            }
        }
    }

    private fun initialiseIndividual(meet: Meet) {
        val endResults = meet.collectEvents(events, convertTo)
                .filter { swimmerFilter.filter(it.swimmer) }
        val swimmers = endResults.swimmers()
        val resultTimes = endResults.resultsTimes(events)
        val resultRanks = endResults.resultRanks(events)
        val totals = endResults.totals()

        calculateWedstrijdzwemmersABove(meet)

        for (swimmerIndex in swimmers.indices) {
            val swimmer = swimmers[swimmerIndex]
            val swimmerTimes = resultTimes[swimmerIndex]
            val swimmerRanks = resultRanks[swimmerIndex]
            val resultMap = results[swimmer] ?: HashMap()

            resultMap["total"] = Pair(totals[swimmerIndex], (swimmerIndex + 1).toString())

            for (eventIndex in events.indices) {
                val event = events[eventIndex]
                val eventName = "${event.distance} ${event.stroke}"

                var rank = swimmerRanks[eventIndex]!!.toInt()
                if (swimmer.isWedstrijdzwemmer().not()) {
                    rank -= wedstrijdzwemmersAbove[event]!![rank - 1]
                }
                if (rank <= 0) {
                    continue
                }
                val resultPair = Pair(swimmerTimes[eventIndex]!!, rank.toString())

                resultMap[eventName] = resultPair
            }

            results[swimmer] = resultMap
        }
    }

    private fun calculateWedstrijdzwemmersABove(meet: Meet) {
        for (event in meet.events) {
            var counter = 0
            val results = event.swimResults(convertTo)
            val wedstrijdszwemmersAbove = ArrayList<Int>()

            results.forEach {
                wedstrijdszwemmersAbove += counter
                if (it.swimmer.isWedstrijdzwemmer()) {
                    counter++
                }
            }

            this.wedstrijdzwemmersAbove[event] = wedstrijdszwemmersAbove
        }
    }

    private fun Swimmer.isWedstrijdzwemmer() = age.readableName.contains("Wz")
}