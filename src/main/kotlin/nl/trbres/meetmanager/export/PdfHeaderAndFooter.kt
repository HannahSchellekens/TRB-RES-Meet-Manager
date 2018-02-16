package nl.trbres.meetmanager.export

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter

/**
 * @author Ruben Schellekens
 */
object PdfHeaderAndFooter : PdfPageEventHelper() {

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        PdfFooter.onEndPage(writer, document)
    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        PdfHeader.onStartPage(writer, document)
    }
}