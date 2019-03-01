package nl.trbres.meetmanager.util

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream

/**
 * The default font used in pdf documents.
 */
var DEFAULT_FONT: Font? = null

/**
 * The default leading for paragraphs.
 */
var DEFAULT_LEADING: Float = 14f

/**
 * Creates a new pdf document,.
 */
fun document(file: File, mutations: Document.(PdfWriter) -> Unit = {}): Document {
    val document = Document()
    val writer = PdfWriter.getInstance(document, FileOutputStream(file))
    document.mutations(writer)
    return document
}

/**
 * Creates a new pdf document.
 */
fun document(fileName: String, mutations: Document.(PdfWriter) -> Unit = {}) = document(File(fileName), mutations)

/**
 * Executes the given mutations on the document within an `open()`..`close()` pair.
 */
fun Document.write(mutations: Document.() -> Unit) {
    open()
    this.mutations()
    close()
}

/**
 * Set the orientation of the document to landscape.
 */
fun Document.landscape() {
    pageSize = PageSize.A4.rotate()
}

/**
 * Adds a paragraph to the document.
 */
fun Document.paragraph(text: String = "", font: Font? = DEFAULT_FONT, leading: Float? = DEFAULT_LEADING,
                       alignment: Int = Paragraph.ALIGN_LEFT, mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = if (leading == null) Paragraph(text, font) else Paragraph(leading, text, font)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Adds a paragraph to the paragraph.
 */
fun Paragraph.paragraph(text: String = "", font: Font? = DEFAULT_FONT, leading: Float? = DEFAULT_LEADING,
                       alignment: Int = Paragraph.ALIGN_LEFT, mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = if (leading == null) Paragraph(text, font) else Paragraph(leading, text, font)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Creates a new paragraph.
 */
fun newParagraph(text: String = "", font: Font? = DEFAULT_FONT, leading: Float? = DEFAULT_LEADING,
              alignment: Int = Paragraph.ALIGN_LEFT, mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = if (leading == null) Paragraph(text, font) else Paragraph(leading, text, font)
    paragraph.alignment = alignment
    paragraph.mutations()
    return paragraph
}

/**
 * Adds a paragraph to the document.
 */
fun Document.paragraph(chunk: Chunk, leading: Float? = DEFAULT_LEADING, alignment: Int = Paragraph.ALIGN_LEFT,
                       mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = if (leading == null) Paragraph(chunk) else Paragraph(leading, chunk)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Adds a paragraph to the paragraph.
 */
fun Paragraph.paragraph(chunk: Chunk, leading: Float? = DEFAULT_LEADING, alignment: Int = Paragraph.ALIGN_LEFT,
                       mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = if (leading == null) Paragraph(chunk) else Paragraph(leading, chunk)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Adds a paragraph to the document.
 */
fun Document.paragraph(phrase: Phrase, alignment: Int = Paragraph.ALIGN_LEFT, mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = Paragraph(phrase)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Adds a paragraph to the paragraph.
 */
fun Paragraph.paragraph(phrase: Phrase, alignment: Int = Paragraph.ALIGN_LEFT, mutations: Paragraph.() -> Unit = {}): Paragraph {
    val paragraph = Paragraph(phrase)
    paragraph.alignment = alignment
    paragraph.mutations()
    add(paragraph);
    return paragraph
}

/**
 * Adds a line separator to the document.
 */
fun Document.separator(spaceBefore: Float = 8f, font: Font? = DEFAULT_FONT, mutations: LineSeparator.() -> Unit = {}): LineSeparator {
    paragraph(" ", font, spaceBefore)
    val separator = LineSeparator()
    separator.mutations()
    add(separator)
    return separator
}

/**
 * Adds a line separator to the paragraph.
 */
fun Paragraph.separator(spaceBefore: Float = 8f, mutations: LineSeparator.() -> Unit = {}): LineSeparator {
    paragraph(" ", font, spaceBefore)
    val separator = LineSeparator()
    separator.mutations()
    add(separator)
    return separator
}

/**
 * Adds some spacing to the document.
 */
fun Document.spacing(spacing: Float = 8f, font: Font? = DEFAULT_FONT, mutatations: Paragraph.() -> Unit = {}): Paragraph {
    val dummyParagraph = paragraph(" ", font, spacing)
    dummyParagraph.mutatations()
    add(dummyParagraph)
    return dummyParagraph
}

/**
 * Adds some spacing to the paragraph.
 */
fun Paragraph.spacing(spacing: Float = 8f, font: Font? = DEFAULT_FONT, mutatations: Paragraph.() -> Unit = {}): Paragraph {
    val dummyParagraph = paragraph(" ", font, spacing)
    dummyParagraph.mutatations()
    add(dummyParagraph)
    return dummyParagraph
}

/**
 * Adds a PdfPTable to the document.
 */
fun Document.table(columns: Int, width: Float = 100f, mutations: PdfPTable.() -> Unit = {}): PdfPTable {
    val table = PdfPTable(columns)
    table.widthPercentage = width
    table.mutations()
    add(table)
    return table
}

/**
 * Adds a PdfPTable to a table.
 */
fun PdfPTable.table(columns: Int, width: Float = 100f, mutations: PdfPTable.() -> Unit = {}): PdfPTable {
    val table = PdfPTable(columns)
    table.widthPercentage = width
    table.mutations()
    addCell(table)
    return table
}

/**
 * Adds a line separator to the table.
 */
fun PdfPTable.separator(spaceBefore: Float = 8f, mutations: LineSeparator.() -> Unit = {}): LineSeparator {
    val separator = LineSeparator()
    separator.mutations()
    cell(Phrase(Chunk(separator))) {
        setSpacingBefore(spaceBefore)
    }
    return separator
}

/**
 * Adds a PdfPCell to a PdfPTable.
 */
fun PdfPTable.cell(phrase: Phrase, alignment: Int = Element.ALIGN_LEFT,
                   border: Int = Rectangle.NO_BORDER, fixedHeight: Float? = null,
                   mutations: PdfPCell.() -> Unit = {}): PdfPCell {
    val cell = PdfPCell(phrase)
    cell.border = border
    cell.horizontalAlignment = alignment
    cell.paddingRight = 6f
    if (fixedHeight != null) {
        cell.fixedHeight = fixedHeight
    }
    cell.mutations()
    addCell(cell)
    return cell
}

/**
 * See [PdfPTable.setWidths].
 */
fun PdfPTable.widths(vararg widths: Int) = setWidths(widths)

/**
 * See [PdfPTable.setWidths].
 */
fun PdfPTable.widths(vararg widthPercentages: Float) = setWidths(widthPercentages)