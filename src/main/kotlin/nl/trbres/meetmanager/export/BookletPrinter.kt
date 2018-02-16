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
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.model.Heat
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
    fun printBooklet(owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(owner) ?: return
        DEFAULT_FONT = Fonts.robotoRegular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(64f, 64f, 50f, 64f)

            writer.pageEvent = PdfHeaderAndFooter

            write {
                // Print events
                for ((i, event) in meet.events.withIndex()) {
                    printEvent(event, i + 1)
                }
            }
        }

        pdfFile.open()
    }

    private fun Document.printEvent(event: Event, eventNumber: Int) {
        val heats = event.heats

        table(1) {
            defaultCell.borderWidth = 0f

            addCell(printEventHeader(event, eventNumber))
            addCell("")
            addCell(printHeat(heats[0], 1, event.heats.size))

            setSpacingAfter(1f)
            keepTogether = true
        }

        for (i in 1 until heats.size) {
            table(1) {
                defaultCell.borderWidth = 0f
                addCell(printHeat(heats[i], i + 1, event.heats.size))
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

    private fun printHeat(heat: Heat, heatNumber: Int, heatAmount: Int) = PdfPTable(1).apply {
        defaultCell.borderWidth = 0f

        cell(newParagraph("Serie %d van %d".format(heatNumber, heatAmount), Fonts.robotoRegularUnderline))

        table(5) {
            widths(.041f, .306f, .306f, .185f, .162f)

            for ((lane, swimmer) in heat.lanes) {
                cell(newParagraph(lane.toString()), Element.ALIGN_RIGHT)
                cell(newParagraph(swimmer.name))
                cell(newParagraph(swimmer.club?.name ?: ""))
                cell(newParagraph(swimmer.age.readableName))
                cell(newParagraph("_____________"))
            }
        }

        setSpacingAfter(if (heatNumber == heatAmount) 8f else 2f)
        keepTogether = true
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(owner: Window? = null): File? {
        val result = FileChooser().apply {
            title = "Programmaboekje opslaan..."
            initialFileName = "Schedule_${State.meet!!.name}.pdf"
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastScheduleDirectory].whenNonNull {
                initialDirectory = it.file()
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastScheduleDirectory] = result.parent
        return result
    }
}