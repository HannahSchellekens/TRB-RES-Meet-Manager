package nl.trbres.meetmanager.export

import com.lowagie.text.Element
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.util.*
import java.io.File
import kotlin.math.ceil

/**
 * @author Hannah Schellekens
 */
object JuryCardPrinter {

    /**
     * Prints all jury cards.
     *
     * The user gets prompted with a dialog to save the pdf.
     */
    @JvmStatic
    fun printCards(owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(owner) ?: return
        DEFAULT_FONT = Fonts.larger

        // Make document.
        document(pdfFile) {
            setMargins(0f, 0f, 0f, 0f)
            landscape()

            write {
                table(3) {
                    widths(1, 1, 1)
                    var cards = 0

                    for ((eventNo, event) in meet.events.withIndex()) {
                        for ((heatNo, _) in event.heats.withIndex()) {
                            val eventNumber = eventNo + 1
                            val eventName = event.toString()
                            val heatNumber = heatNo + 1

                            cards++
                            card(
                                    eventNumber.toString(),
                                    eventName,
                                    heatNumber.toString(),
                                    meet.lanes,
                                    event.metric
                            )
                        }
                    }

                    // Fill blank spots.
                    while (cards % 6 != 0) {
                        emptyCard()
                        cards++
                    }
                }
            }
        }

        pdfFile.open()
    }

    /**
     * Prints a card without any information.
     */
    private fun PdfPTable.emptyCard() = card(" ", " ", "   ", 1..8, metric = Event.Metric.TIME)

    /**
     * Prints a card given raw values.
     */
    private fun PdfPTable.card(
            eventNumber: String,
            eventName: String,
            heatNumber: String,
            lanes: IntRange,
            metric: Event.Metric
    ) {
        table(4) {
            widths(3, 18, 18, 3)

            val regular = Fonts.larger
            val small = Fonts.small

            // Header
            cell(newParagraph())
            val cell = PdfPCell(PdfPTable(2).apply {
                widths(2, 4)

                cell(newParagraph("Programma $eventNumber", regular))
                cell(newParagraph("Serie $heatNumber", regular), alignment = Element.ALIGN_RIGHT)
            }).apply {
                paddingTop = 12f
                colspan = 2
                borderWidth = 0f
            }
            addCell(cell)
            cell(newParagraph())

            cell(newParagraph())
            cell(newParagraph(""), border = Rectangle.BOTTOM) {
                colspan = 2
                fixedHeight = 0f
            }
            cell(newParagraph())

            // Name & Event
            cell(newParagraph())
            cell(newParagraph(eventName, regular), alignment = Element.ALIGN_CENTER, border = Rectangle.BOTTOM) {
                colspan = 2
                paddingBottom = 12f
                paddingTop = 10f
            }
            cell(newParagraph())

            // End Time
            cell(newParagraph())
            cell(newParagraph("volgorde " + if (metric == Event.Metric.TIME) "van aankomst" else "", small), border = Rectangle.BOTTOM) {
                colspan = 2
                paddingTop = 8f
                paddingBottom = 32f
            }
            cell(newParagraph())

            // Disqualifications
            if (metric == Event.Metric.TIME) {
                cell(newParagraph())
                cell(newParagraph("diskwalificaties", small)) {
                    colspan = 2
                    paddingTop = 8f
                }
                cell(newParagraph())

                for (i in 1..6) {
                    cell(newParagraph())
                    cell(newParagraph("Baan ____ Reden:", regular)) {
                        colspan = 2
                        paddingTop = 9.25f
                    }
                    cell(newParagraph())
                }
            }
            // Distances
            else if (metric == Event.Metric.DISTANCE) {
                cell(newParagraph())
                cell(newParagraph("afstanden", small)) {
                    colspan = 2
                    paddingTop = 8f
                }
                cell(newParagraph())

                val laneNumbers = lanes.toMutableList()
                val n = laneNumbers.size
                for (i in 0 until ceil(laneNumbers.size / 2f).toInt()) {
                    val firstLane = "Baan ${laneNumbers[i]}: ________ meter"
                    val secondLane = laneNumbers.getOrNull(i + ceil(n / 2f).toInt())?.let { "Baan $it: ________ meter" }
                            ?: ""

                    cell(newParagraph())
                    cell(newParagraph(firstLane, regular)) {
                        paddingTop = 12f
                    }
                    cell(newParagraph(secondLane, regular)) {
                        paddingTop = 12f
                        paddingLeft = 13f
                    }
                    cell(newParagraph())
                }
                cell(newParagraph()) {
                    colspan = 4
                }
            }

            // Footer
            cell(newParagraph())
            cell(newParagraph("paraaf", small), alignment = Element.ALIGN_RIGHT) {
                colspan = 2
                fixedHeight = 48f
                paddingTop = 12f
            }
            cell(newParagraph())
        }
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(owner: Window? = null): File? {
        val meetName = State.meet!!.name
                .replace(" ", "")
                .replace(Regex("[^A-Za-z()\\-0-9&]"), "-")

        val result = FileChooser().apply {
            title = "Jurykaartjes opslaan..."
            initialFileName = "JuryCards_$meetName.pdf"
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastExportDirectory].whenNonNull {
                try {
                    initialDirectory = it.file()
                }
                catch (ignored: Exception) {
                }
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastExportDirectory] = result.parent
        return result
    }
}