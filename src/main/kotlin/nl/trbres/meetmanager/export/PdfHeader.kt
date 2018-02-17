package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import nl.trbres.meetmanager.State

/**
 * @author Ruben Schellekens
 */
object PdfHeader : PdfPageEventHelper() {

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        val meet = State.meet ?: return
        document ?: return
        val cb = writer?.directContent ?: return
        val meetName = Phrase("${meet.name}, ${meet.location}", Fonts.small)
        val placeDate = Phrase("Pagina ${writer.currentPageNumber}", Fonts.small)
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