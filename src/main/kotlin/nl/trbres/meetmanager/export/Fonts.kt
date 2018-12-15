package nl.trbres.meetmanager.export

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import nl.trbres.meetmanager.Resources

/**
 * @author Ruben Schellekens
 */
object Fonts {

    val small = FontFactory.getFont("Roboto-Regular", 7f)!!
    val larger = FontFactory.getFont("Roboto-Regular", 9f)!!
    val regular = FontFactory.getFont("Roboto-Regular", 8f)!!
    val bold = FontFactory.getFont("Roboto-Regular", 8f, Font.BOLD)!!
    val italic = FontFactory.getFont("Roboto-Regular", 8f, Font.ITALIC)!!
    val underline = FontFactory.getFont("Roboto-Regular", 8f, Font.UNDERLINE)!!
    val boldItalic = FontFactory.getFont("Roboto-Regular", 8f, Font.BOLDITALIC)!!

    init {
        FontFactory.register(Resources.FONT_ROBOTO, "Roboto-Regular")
    }
}