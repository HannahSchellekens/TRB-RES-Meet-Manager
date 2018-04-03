package nl.trbres.meetmanager.export

import com.lowagie.text.Element
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.filter.AgeGroupFilter
import nl.trbres.meetmanager.filter.SwimmerFilter
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.CollectedResult
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.util.*
import java.io.File

/**
 * @author Ruben Schellekens
 */
object EndResultPrinter {

    /**
     * Prints the results of a given set of events to PDF.
     *
     * The user gets prompted with a dialog to save the pdf.
     *
     * @param events
     *          The swim events that have to be combined in the end results.
     * @param eventNumbers
     *          The numbers/identifier of the events in the `events` parameter.
     * @param swimmerFilter
     *          A filter that preprocesses swimmers: all matching swimmers get removed from the result list.
     * @param convertTo
     *          Converts all distances of the events to a given amount of metres.
     *          E.g. a value of `100` will convert all results to 100m times.
     *          So if you have a `1:03.30` on a 50m event, the result will become `2:06.60`.
     * @param owner
     *          The parent window that dialogs will centre on. `null` if there is no owner.
     */
    @JvmStatic
    fun printResults(events: List<Event>, eventNumbers: List<Int>, swimmerFilter: SwimmerFilter = SwimmerFilter.NO_FILTER,
                     convertTo: Int?, owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(eventNumbers, swimmerFilter, owner = owner) ?: return
        DEFAULT_FONT = Fonts.regular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(64f, 64f, 40f, 54f)
            pageSize = PageSize.A4.rotate()

            writer.pageEvent = PdfFooter

            write {
                // Title
                paragraph(meet.name, alignment = Paragraph.ALIGN_CENTER)
                paragraph("${meet.location}, ${meet.date}", alignment = Paragraph.ALIGN_CENTER)
                separator()
                spacing(4f)

                // Header
                table(3) {
                    cell(newParagraph("Programma's ${eventNumbers.joinToString(", ")}"))
                    cell(newParagraph("Einduitslag")) {
                        horizontalAlignment = Rectangle.ALIGN_CENTER
                    }

                    val ages = if (swimmerFilter !is AgeGroupFilter) {
                        events.flatMap { it.ages }.distinct().joinToString(", ")
                    }
                    else swimmerFilter.group.toString()
                    cell(newParagraph("${category(events)}, $ages")) {
                        horizontalAlignment = Rectangle.ALIGN_RIGHT
                    }
                }
                spacing(4f)
                separator(3f)
                spacing(4f)

                // Result table.
                val endResults = meet.collectEvents(events, convertTo)
                        .filter { swimmerFilter.filter(it.swimmer) }

                val ranks = endResults.ranks()
                val names = endResults.names()
                val clubs = endResults.clubs()

                val resultTimes = endResults.resultsTimes(events)
                val resultRanks = endResults.resultRanks(events)
                val totals = endResults.totals()

                val n = events.size
                val width = timeColumnWidth(n)
                val columns = (1..n).flatMap { listOf(width, 3.37f) }.toFloatArray() + width

                table(3 + 2 * n + 1) {
                    widths(*(floatArrayOf(3.93f, 18f, 18f) + columns))
                    val leading = 0.875f

                    cell(newParagraph("rang", Fonts.small), Element.ALIGN_RIGHT)
                    cell(newParagraph("naam", Fonts.small))
                    cell(newParagraph("vereniging", Fonts.small))
                    for (i in 0 until n) {
                        cell(newParagraph("${events[i].distance.metres}m ${events[i].stroke.strokeName.toLowerCase()}", Fonts.small), Element.ALIGN_RIGHT)
                        cell(newParagraph("", Fonts.small))
                    }
                    cell(newParagraph("totaal", Fonts.small), Element.ALIGN_RIGHT)

                    for (swimmerIndex in 0 until names.size) {
                        cell(newParagraph(ranks[swimmerIndex]), Element.ALIGN_RIGHT) {
                            setLeading(1f, leading)
                        }
                        cell(newParagraph(names[swimmerIndex])) {
                            setLeading(1f, leading)
                        }
                        cell(newParagraph(clubs[swimmerIndex])) {
                            setLeading(1f, leading)
                        }
                        for (eventIndex in 0 until n) {
                            cell(newParagraph(resultTimes[swimmerIndex][eventIndex]!!), Element.ALIGN_RIGHT) {
                                setLeading(1f, leading)
                            }
                            cell(newParagraph(resultRanks[swimmerIndex][eventIndex]!!)) {
                                setLeading(1f, leading)
                            }
                        }
                        cell(newParagraph(totals[swimmerIndex], Fonts.bold), Element.ALIGN_RIGHT) {
                            setLeading(1f, leading)
                        }
                    }
                }
            }
        }

        pdfFile.open()
    }

    /**
     * Generates the category name.
     */
    private fun category(events: List<Event>): String {
        val base = events.first().category
        if (base == Category.MIX) {
            return events.first().ages.first()[base]
        }
        for (i in 1 until events.size) {
            val cat = events[i].category
            when (cat) {
                Category.MIX -> return events[i].ages.first()[cat]
                else -> {
                    if (cat != base) {
                        return events[i].ages.first()[cat]
                    }
                }
            }
        }
        return events.first().ages.first()[base]
    }

    /**
     * The width of a result column's time.
     */
    private fun timeColumnWidth(columns: Int): Float {
        return (60.07f - columns * 3.37f) / (columns + 1f)
    }

    /**
     * Generates all rank numbers/statusses in order.
     */
    private fun List<CollectedResult>.ranks() = mapIndexed { index, _ -> ((index + 1).toString() + ".") }

    /**
     * Generates all the names that should be put on the event list (in order).
     */
    private fun List<CollectedResult>.names() = map { it.swimmer.name }

    /**
     * Generates all club names in order.
     */
    private fun List<CollectedResult>.clubs() = map { it.swimmer.club?.name ?: "" }

    /**
     * Generates all total times in order.
     */
    private fun List<CollectedResult>.totals() = map { it.total.toString() }

    /**
     * Generates a list with maps that map event indices (in the events list) numbers to results.
     */
    private fun List<CollectedResult>.resultsTimes(events: List<Event>) = indexRange().map {
        val result = this[it]
        events.mapIndexed { index, event ->
            index to (result.results[event]?.result?.toString() ?: "")
        }.toMap()
    }

    /**
     * Generates a list with maps that map event indices (in the events list) numbers to ranks.
     */
    private fun List<CollectedResult>.resultRanks(events: List<Event>) = indexRange().map { rankindex ->
        val result = this[rankindex]
        events.mapIndexed { index, event ->
            index to ((event.swimResults().indexOfFirst { it.swimmer.id == result.swimmer.id } + 1).toString() + ".")
        }.toMap()
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(numbers: List<Int>, swimmerFilter: SwimmerFilter, owner: Window? = null): File? {
        val fileNameSuffix = if (swimmerFilter is AgeGroupFilter) {
            "_${swimmerFilter.group.readableName.toLowerCase().replace(" ", "-")}"
        }
        else ""

        val result = FileChooser().apply {
            title = "Uitslag opslaan..."
            initialFileName = "EndResultList_%s%s.pdf".format(numbers.joinToString("-"), fileNameSuffix)
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastExportDirectory].whenNonNull {
                try {
                    initialDirectory = it.file()
                }
                catch (ignored: Exception) { }
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastExportDirectory] = result.parent
        return result
    }
}