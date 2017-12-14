package nl.trbres.meetmanager.export

import com.lowagie.text.Element
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
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
     */
    @JvmStatic
    fun printResults(events: List<Event>, eventNumbers: List<Int>, owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(eventNumbers, owner = owner) ?: return
        DEFAULT_FONT = Fonts.robotoRegular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(64f, 64f, 40f, 40f)
            pageSize = PageSize.A4.rotate()

            writer.pageEvent = EventResultPrinter.ResultListFooter

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
                    cell(newParagraph("Junioren")) {
                        horizontalAlignment = Rectangle.ALIGN_RIGHT
                    }
                }
                spacing(4f)
                separator(3f)
                spacing(4f)

                // Result table.
                val endResults = meet.collectEvents(events)

                val ranks = endResults.ranks()
                val names = endResults.names()
                val clubs = endResults.clubs()

                val school = listOf("34.42", "32.78")
                val schoolRank = listOf("2.", "1.")
                val rug = listOf("29.57", "31.00")
                val rugRank = listOf("1.", "2.")
                val vrij = listOf("25.26", "25.48")
                val vrijRank = listOf("1.", "2.")
                val totaal = listOf("1:29.25", "1:29.26")

                table(11) {
                    widths(35, 180, 170, 104, 30, 104, 30, 104, 30, 104)

                    cell(newParagraph("rang", Fonts.robotoSmall), Element.ALIGN_RIGHT)
                    cell(newParagraph("naam", Fonts.robotoSmall))
                    cell(newParagraph("vereniging", Fonts.robotoSmall))
                    cell(newParagraph("50m schoolslag", Fonts.robotoSmall), Element.ALIGN_RIGHT)
                    cell(newParagraph("", Fonts.robotoSmall))
                    cell(newParagraph("50m rugslag", Fonts.robotoSmall), Element.ALIGN_RIGHT)
                    cell(newParagraph("", Fonts.robotoSmall))
                    cell(newParagraph("50m vrije slag", Fonts.robotoSmall), Element.ALIGN_RIGHT)
                    cell(newParagraph("", Fonts.robotoSmall))
                    cell(newParagraph("totaal", Fonts.robotoSmall), Element.ALIGN_RIGHT)

                    for (i in 0 until names.size) {
                        cell(newParagraph(ranks[i]), Element.ALIGN_RIGHT)
                        cell(newParagraph(names[i]))
                        cell(newParagraph(clubs[i]))
                        cell(newParagraph(school[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                        cell(newParagraph(schoolRank[i]))
                        cell(newParagraph(rug[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                        cell(newParagraph(rugRank[i]))
                        cell(newParagraph(vrij[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                        cell(newParagraph(vrijRank[i]))
                        cell(newParagraph(totaal[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                    }
                }
            }
        }

        pdfFile.open()
    }

    /**
     * Generates all rank numbers/statusses in order.
     */
    private fun List<CollectedResult>.ranks() = emptyList<String>()

    /**
     * Generates all the names that should be put on the event list (in order).
     */
    private fun List<CollectedResult>.names() = emptyList<String>()

    /**
     * Generates all club names in order.
     */
    private fun List<CollectedResult>.clubs() = emptyList<String>()

    /**
     * Generates all results in order.
     */
    private fun List<CollectedResult>.results() = emptyList<String>()

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(numbers: List<Int>, owner: Window? = null): File? {
        val result = FileChooser().apply {
            title = "Uitslag opslaan..."
            initialFileName = "EndResultList_%02d.pdf".format(numbers.joinToString("-"))
            extensionFilters += FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf")
            UserSettings[UserSettings.Key.lastExportDirectory].whenNonNull {
                initialDirectory = it.file()
            }
        }.showSaveDialog(owner) ?: return null

        UserSettings[UserSettings.Key.lastExportDirectory] = result.parent
        return result
    }
}