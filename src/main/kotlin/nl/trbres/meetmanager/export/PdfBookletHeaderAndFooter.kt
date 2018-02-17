package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter

/**
 * @author Ruben Schellekens
 */
object PdfBookletHeaderAndFooter : PdfPageEventHelper() {

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        PdfBookletFooter.onEndPage(writer, document)
    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        PdfBookletHeader.onStartPage(writer, document)
    }
}