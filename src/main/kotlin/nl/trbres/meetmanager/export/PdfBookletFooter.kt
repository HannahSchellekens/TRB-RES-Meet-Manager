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
object PdfBookletFooter : PdfPageEventHelper() {

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        document ?: return
        val cb = writer?.directContent ?: return
        val club = Phrase(State.meet?.organiser ?: "TRB-RES", Fonts.small)
        val date = State.meet?.date ?: Date()
        val timestamp = Phrase("$date", Fonts.small)
        val separator = LineSeparator()
        separator.drawLine(cb,
                document.leftMargin(),
                document.right(),
                document.bottom() + 2
        )
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                club,
                document.leftMargin(),
                document.bottom() - 8, 0f
        )
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                timestamp,
                document.right(),
                document.bottom() - 8, 0f
        )
    }
}