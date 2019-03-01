package nl.trbres.meetmanager.export

import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import javafx.stage.FileChooser
import javafx.stage.Window
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.model.SwimResult
import nl.trbres.meetmanager.util.*
import java.io.File

/**
 * @author Ruben Schellekens
 */
object EventResultPrinter {

    /**
     * Prints the results of the given event to a PDF.
     *
     * The user gets prompted with a dialog to save the pdf.
     */
    @JvmStatic
    fun printResults(event: Event, eventNumber: Int, owner: Window? = null) {
        val meet = State.meet ?: error("No meet selected")
        val pdfFile = promptSaveLocation(eventNumber, owner) ?: return
        DEFAULT_FONT = Fonts.regular

        // Make document.
        document(pdfFile) { writer ->
            setMargins(48f, 48f, 20f, 40f)

            writer.pageEvent = PdfFooter

            write {
                // Title
                paragraph(meet.name, alignment = Paragraph.ALIGN_CENTER)
                paragraph("${meet.location}, ${meet.date}", alignment = Paragraph.ALIGN_CENTER)
                separator()
                spacing(4f)

                // Header
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
                spacing(4f)
                separator(3f)
                spacing(4f)

                // Result table.
                val swimResults: List<SwimResult> = event.swimResults()
                val ranks = swimResults.ranks()
                val names = swimResults.names()
                val birthYears = swimResults.birthYears()
                val clubs = swimResults.clubs()
                val ages = swimResults.ages()
                val results = swimResults.results()
                val messages = swimResults.messages()

                table(6) {
                    widths(.051f, .319f, .319f, .159f, .04f, .112f)
                    val leading = 0.875f

                    cell(newParagraph("rang", Fonts.small), Element.ALIGN_RIGHT)
                    cell(newParagraph("naam", Fonts.small))
                    cell(newParagraph("vereniging", Fonts.small))
                    cell(newParagraph("leeftijd", Fonts.small))
                    cell(newParagraph("", Fonts.small))
                    cell(newParagraph("eindtijd", Fonts.small), Element.ALIGN_RIGHT)

                    for (i in 0 until names.size) {
                        cell(newParagraph(ranks[i]), Element.ALIGN_RIGHT) {
                            setLeading(0f, leading)
                        }
                        cell(newParagraph(names[i])) {
                            setLeading(0f, leading)
                        }
                        cell(newParagraph(clubs[i])) {
                            setLeading(0f, leading)
                        }
                        cell(newParagraph(ages[i])) {
                            setLeading(0f, leading)
                        }
                        cell(newParagraph(birthYears[i])) {
                            setLeading(0f, leading)
                        }
                        cell(newParagraph(results[i], Fonts.bold), Element.ALIGN_RIGHT) {
                            setLeading(0f, leading)
                        }

                        // Relay participants.
                        val swimmer = swimResults[i].swimmer
                        if (swimmer is Relay) {
                            cell(newParagraph(""))
                            cell(newParagraph(swimmer.members.joinToString(", ") { it.nameWithBirthYear }, Fonts.small)) {
                                paddingLeft = 12f
                                colspan = 5
                                setLeading(0f, leading)
                            }
                        }

                        // Special message?
                        val message = messages[i]
                        if (message != null) {
                            cell(newParagraph(""))
                            cell(newParagraph(message, Fonts.italic)) {
                                colspan = 5
                                setLeading(0f, leading)
                            }
                        }
                    }
                }
            }
        }

        pdfFile.open()
    }

    /**
     * Generates all rank numbers/statusses in order.
     */
    private fun List<SwimResult>.ranks() = mapIndexed { i, result ->
        result.status?.type?.abbreviation ?: "${i + 1}."
    }

    /**
     * Generates all the names that should be put on the event list (in order).
     */
    private fun List<SwimResult>.names() = map { it.swimmer.name }

    /**
     * Generates all the birth years that should be put on the event list (in order).
     */
    private fun List<SwimResult>.birthYears() = map { it.swimmer.birthYearDigits }

    /**
     * Generates all club names in order.
     */
    private fun List<SwimResult>.clubs() = map { it.swimmer.club?.name ?: "" }

    /**
     * Generates all ages in order.
     */
    private fun List<SwimResult>.ages() = map { it.swimmer.age.readableName }

    /**
     * Generates all results in order.
     */
    private fun List<SwimResult>.results() = map {
        if (it.result.isZero()) "" else it.result.toString()
    }

    /**
     * Generates all special messages of the results in order.
     */
    private fun List<SwimResult>.messages(): List<String?> = map {
        if (it.disqualification != null) {
            it.disqualification.fullMessage()
        }
        else null
    }

    /**
     * Shows a [FileChooser] to pick a saving location.
     */
    private fun promptSaveLocation(number: Int, owner: Window? = null): File? {
        val result = FileChooser().apply {
            title = "Uitslag opslaan..."
            initialFileName = "ResultList_%02d.pdf".format(number)
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