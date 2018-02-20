package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPTable
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.model.Heat
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.util.*
import java.io.File

/**
 * @author Ruben Schellekens
 */
object BookletPrinter {

    /**
     * Prints a booklet containing the schedule of the meet.
     *
     * The user gets prompted with a dialog to save the pdf.
     */
    @JvmStatic
    fun printBooklet(owner: Window? = null, highlight: Club? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(owner, highlight) ?: return
        DEFAULT_FONT = Fonts.regular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(64f, 64f, 50f, 64f)

            writer.pageEvent = PdfBookletHeaderAndFooter

            write {
                // Print events
                for ((i, event) in meet.events.withIndex()) {
                    printEvent(highlight, event, i + 1)
                }
            }
        }

        pdfFile.open()
    }

    private fun Document.printEvent(highlight: Club?, event: Event, eventNumber: Int) {
        val heats = event.heats

        table(1) {
            defaultCell.borderWidth = 0f

            addCell(printEventHeader(event, eventNumber))

            if (heats.isNotEmpty()) {
                addCell("")
                addCell(printHeat(highlight, heats[0], 1, event.heats.size))
            }

            setSpacingAfter(1f)
            keepTogether = true
        }

        for (i in 1 until heats.size) {
            table(1) {
                defaultCell.borderWidth = 0f
                addCell(printHeat(highlight, heats[i], i + 1, event.heats.size))
            }
        }
    }

    private fun printEventHeader(event: Event, eventNumber: Int) = PdfPTable(1).apply {
        defaultCell.borderWidthTop = 0f
        defaultCell.borderWidthLeft = 0f
        defaultCell.borderWidthRight = 0f
        defaultCell.borderWidthBottom = 1f
        defaultCell.paddingBottom = 8f

        table(3) {
            cell(Paragraph(DEFAULT_LEADING, "Programma $eventNumber", DEFAULT_FONT), Rectangle.NO_BORDER)
            with(event) {
                cell(Paragraph(DEFAULT_LEADING, "${ages.first()[category]}, $distance $stroke", DEFAULT_FONT), Rectangle.NO_BORDER) {
                    horizontalAlignment = Rectangle.ALIGN_CENTER
                }
            }
            cell(Paragraph(DEFAULT_LEADING, event.ages.joinToString(", "), DEFAULT_FONT), Rectangle.NO_BORDER) {
                horizontalAlignment = Rectangle.ALIGN_RIGHT
            }
        }
    }

    private fun printHeat(highlight: Club?, heat: Heat, heatNumber: Int, heatAmount: Int) = PdfPTable(1).apply {
        defaultCell.borderWidth = 0f

        cell(newParagraph("Serie %d van %d".format(heatNumber, heatAmount), Fonts.underline))

        table(5) {
            widths(.041f, .306f, .306f, .185f, .162f)

            defaultCell.paddingBottom = 0f

            for ((lane, swimmer) in heat.lanes.toSortedMap()) {
                val font = if (swimmer.club == highlight && highlight != null) {
                    Fonts.boldItalic
                }
                else Fonts.regular

                cell(newParagraph(lane.toString(), font), Element.ALIGN_RIGHT)
                cell(newParagraph(swimmer.name, font))
                cell(newParagraph(swimmer.club?.toString() ?: "", font))
                cell(newParagraph(swimmer.age.readableName, font))
                cell(newParagraph("_____________"))

                // When relay team, print members.
                if (swimmer is Relay) {
                    cell(newParagraph(""))
                    cell(newParagraph(swimmer.members.joinToString(", ") { it.name }, Fonts.small)) {
                        paddingLeft = 12f
                        colspan = 4
                    }
                }
            }
        }

        setSpacingAfter(if (heatNumber == heatAmount) 8f else 2f)
        keepTogether = true
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(owner: Window? = null, highlight: Club?): File? {
        val clubSuffix = if (highlight != null) {
            "_" + highlight.name
                    .replace(" ", "")
                    .replace(Regex("[^A-Za-z()\\-0-9&]"), "-")
        }
        else ""

        val meetName = State.meet!!.name
                .replace(" ", "")
                .replace(Regex("[^A-Za-z()\\-0-9&]"), "-")

        val result = FileChooser().apply {
            title = "Programmaboekje opslaan..."
            initialFileName = "Schedule_$meetName$clubSuffix.pdf"
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastScheduleDirectory].whenNonNull {
                initialDirectory = it.file()
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastScheduleDirectory] = result.parent
        return result
    }
}