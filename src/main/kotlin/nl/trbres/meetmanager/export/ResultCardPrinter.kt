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
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.util.*
import java.io.File

/**
 * @author Hannah Schellekens
 */
object ResultCardPrinter {

    /**
     * Prints all result cards.
     *
     * The user gets prompted with a dialog to save the pdf.
     */
    @JvmStatic
    fun printCards(owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        if (meet.events.all { it.heats.isEmpty() }) return
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
                        for ((heatNo, heat) in event.heats.withIndex()) {
                            for ((laneNo, swimmer) in heat.lanes.entries) {
                                val eventNumber = eventNo + 1
                                val swimmerName = swimmer.name
                                val clubName = swimmer.club?.name ?: ""
                                val eventName = event.toString()
                                val heatNumber = heatNo + 1
                                val members = if (swimmer is Relay) {
                                    swimmer.members.map { it.name }
                                }
                                else emptyList()

                                cards++
                                card(
                                        eventNumber.toString(),
                                        swimmerName,
                                        clubName.replace("<Geen>", ""),
                                        eventName,
                                        heatNumber.toString(),
                                        laneNo.toString(),
                                        members,
                                        event.metric
                                )
                            }
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
    private fun PdfPTable.emptyCard() = card(" ", " ", " ", " ", "   ", "   ", emptyList(), metric = Event.Metric.TIME)

    /**
     * Prints a card given raw values.
     */
    private fun PdfPTable.card(
            eventNumber: String,
            swimmerName: String,
            clubName: String,
            eventName: String,
            heatNumber: String,
            laneNumber: String,
            members: List<String>,
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
                cell(newParagraph(clubName, regular), alignment = Element.ALIGN_RIGHT)
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
            cell(newParagraph(swimmerName, regular), alignment = Element.ALIGN_CENTER) {
                colspan = 2
                paddingTop = 10f
            }
            cell(newParagraph())

            cell(newParagraph(eventName, regular), alignment = Element.ALIGN_CENTER) {
                colspan = 4
                paddingBottom = 12f
            }

            // Relay members
            if (members.isNotEmpty()) {
                cell(newParagraph())
                cell(newParagraph(members.joinToString("\n"), small), alignment = Element.ALIGN_CENTER, border = Rectangle.BOTTOM or Rectangle.TOP) {
                    colspan = 2
                    paddingBottom = 6f
                    setLeading(0f, 1.2f)
                }
                cell(newParagraph())
            }

            // Heat/Lane
            cell(newParagraph())
            cell(newParagraph("Serie $heatNumber      Baan $laneNumber", regular), alignment = Element.ALIGN_CENTER, border = Rectangle.TOP) {
                colspan = 2
                paddingTop = 6f
                paddingBottom = 5f
            }
            cell(newParagraph())

            cell(newParagraph())
            cell(newParagraph(""), border = Rectangle.BOTTOM) {
                colspan = 2
                fixedHeight = 0f
            }
            cell(newParagraph())

            // End Time
            cell(newParagraph())
            cell(newParagraph(metric.endResult, small)) {
                colspan = 2
                paddingTop = 16f
            }
            cell(newParagraph())

            cell(newParagraph())
            val resultLine = if (metric == Event.Metric.TIME) "_______ min _______ sec _______ 1/100" else "___________ meter"
            cell(newParagraph(resultLine, regular), alignment = Element.ALIGN_CENTER) {
                colspan = 2
                paddingTop = 16f
            }
            cell(newParagraph())

            // Footer
            cell(newParagraph())
            cell(newParagraph("diskwalificatie: ja/nee\nreden:", small)) {
                colspan = 2
                paddingTop = 14f
                fixedHeight = if (members.isEmpty()) {
                    96.4f
                }
                else 77f - ((4f + members.size) * (members.size - 1))
            }
            cell(newParagraph())

            cell(newParagraph())
            cell(newParagraph("paraaf", small), alignment = Element.ALIGN_RIGHT) {
                colspan = 2
                fixedHeight = 48f
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
            title = "Tijdwaarnemingskaartjes opslaan..."
            initialFileName = "ResultCards_$meetName.pdf"
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