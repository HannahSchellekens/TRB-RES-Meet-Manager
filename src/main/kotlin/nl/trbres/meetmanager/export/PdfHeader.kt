package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.time.Date

/**
 * @author Ruben Schellekens
 */
object PdfHeader : PdfPageEventHelper() {

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        val meet = State.meet ?: return
        document ?: return
        val cb = writer?.directContent ?: return
        val meetName = Phrase(meet.name, Fonts.robotoSmall)
        val date = Date()
        val placeDate = Phrase("${meet.location}, $date", Fonts.robotoSmall)
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                meetName,
                document.leftMargin(),
                document.top() - 2 + 10, 0f
        )
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                placeDate,
                document.right(),
                document.top() - 2 + 10, 0f
        )
        val separator = LineSeparator()
        separator.drawLine(cb,
                document.leftMargin(),
                document.right(),
                document.top() - 8 + 10
        )
    }
}