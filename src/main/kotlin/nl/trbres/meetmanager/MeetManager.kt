package nl.trbres.meetmanager

import com.lowagie.text.Element
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.Rectangle.ALIGN_CENTER
import com.lowagie.text.Rectangle.NO_BORDER
import javafx.application.Application
import nl.trbres.meetmanager.export.EventResultPrinter
import nl.trbres.meetmanager.export.Fonts
import nl.trbres.meetmanager.util.*
import nl.trbres.meetmanager.view.MainView
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.setStageIcon
import java.io.File

/**
 * @author Ruben Schellekens
 */
class MeetManagerApp : App(MainView::class) {

    init {
        setStageIcon(Icons.trbResLogo)
        importStylesheet("/style/main.css")
    }
}

fun main(args: Array<String>) {
    debugPdf()
    System.exit(1)
    State.meet = json.deserialize()
    Application.launch(MeetManagerApp::class.java, *args)
}

val json = """{"name":"Swimkick Deel 1 Met Ruubje","location":"Tilburg","date":{"year":2017,"month":12,"day":8},"lanes":{"start":2,"end":5},"events":[{"distance":{"metres":50,"times":1,"title":"50m"},"stroke":"BACKSTROKE","category":"MALE","ages":[{"default":"SENIOREN"}],"heats":[{"lanes":{"3":{"name":"Ruubje Schellekens","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"926dc895-42c3-4ab1-b7f1-623395c7e5ec"},"4":{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"}},"results":{"3":{"hours":0,"minutes":0,"seconds":29,"hundreths":57},"4":{"hours":0,"minutes":0,"seconds":31,"hundreths":0}},"statusses":{}}]},{"distance":{"metres":50,"times":1,"title":"50m"},"stroke":"BACKSTROKE","category":"FEMALE","ages":[{"default":"JUNIOREN"}],"heats":[{"lanes":{"2":{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"},"3":{"name":"Frana Renssen","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"3e990c6f-b7e9-4a90-9729-22604ad3d641"},"4":{"name":"Kleintje Vissers","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"efc0b018-8638-4079-973b-b36b4fb442ff"}},"results":{"3":{"hours":0,"minutes":0,"seconds":32,"hundreths":84}},"statusses":{"2":{"type":"DID_NOT_START","reason":null},"4":{"type":"DISQUALIFIED","reason":"Te vroeg bewogen voor de start."}}}]}],"clubs":[{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"}],"swimmers":[{"name":"Ruubje Schellekens","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"926dc895-42c3-4ab1-b7f1-623395c7e5ec"},{"name":"Frana Renssen","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"3e990c6f-b7e9-4a90-9729-22604ad3d641"},{"name":"Kleintje Vissers","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"efc0b018-8638-4079-973b-b36b4fb442ff"},{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"}]}"""

fun debugPdf() {
    val pdfFile = File("test.pdf")
    DEFAULT_FONT = Fonts.robotoRegular

    // Make document.
    document(pdfFile) { writer ->
        setMargins(64f, 64f, 40f, 40f)
        pageSize = PageSize.A4.rotate()

        writer.pageEvent = EventResultPrinter.ResultListFooter

        write {
            // Title
            paragraph("Swimkick Deel 1", alignment = Paragraph.ALIGN_CENTER)
            paragraph("Tilburg, 2017-12-16", alignment = Paragraph.ALIGN_CENTER)
            separator()
            spacing(4f)

            // Header
            table(3) {
                cell(Paragraph(DEFAULT_LEADING, "Programma's 2, 7, 12", DEFAULT_FONT), NO_BORDER)
                cell(Paragraph(DEFAULT_LEADING, "Einduitslag", DEFAULT_FONT), NO_BORDER) {
                    horizontalAlignment = ALIGN_CENTER
                }
                cell(Paragraph(DEFAULT_LEADING, "Junioren", DEFAULT_FONT), NO_BORDER) {
                    horizontalAlignment = Rectangle.ALIGN_RIGHT
                }
            }
            spacing(4f)
            separator(3f)
            spacing(4f)

            // Result table.
            val ranks = listOf("1.", "2.")
            val names = listOf("Ruben Schellekens", "Lars Hurks")
            val clubs = listOf("Trb/Res", "Trb/Res")

            val school = listOf("34.42", "32.78")
            val schoolRank = listOf("2.", "1.")
            val rug = listOf("29.57", "31.00")
            val rugRank = listOf("1.", "2.")
            val vrij = listOf("25.26", "25.48")
            val vrijRank = listOf("1.", "2.")
            val totaal = listOf("1:29.25", "1:29.26")

            table(10) {
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
                    cell(newParagraph(school[i], Fonts.robotoRegular), Element.ALIGN_RIGHT)
                    cell(newParagraph(schoolRank[i]))
                    cell(newParagraph(rug[i], Fonts.robotoRegular), Element.ALIGN_RIGHT)
                    cell(newParagraph(rugRank[i]))
                    cell(newParagraph(vrij[i], Fonts.robotoRegular), Element.ALIGN_RIGHT)
                    cell(newParagraph(vrijRank[i]))
                    cell(newParagraph(totaal[i], Fonts.robotoBold), Element.ALIGN_RIGHT)
                }
            }
        }
    }
}