package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import nl.trbres.meetmanager.time.Date
import nl.trbres.meetmanager.time.Time

/**
 * @author Ruben Schellekens
 */
object PdfFooter : PdfPageEventHelper() {

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        document ?: return
        val cb = writer?.directContent ?: return
        val club = Phrase("TRB-RES", Fonts.small)
        val now = Time()
        val date = Date()
        val timestamp = Phrase("$date, %02du%02d".format(now.hours, now.minutes), Fonts.small)
        val separator = LineSeparator()
        separator.drawLine(cb,
                document.leftMargin(),
                document.right(),
                document.bottom() + 8 - 14
        )
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                club,
                document.leftMargin(),
                document.bottom() - 4 - 14, 0f
        )
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                timestamp,
                document.right(),
                document.bottom() - 4 - 14, 0f
        )
    }
}