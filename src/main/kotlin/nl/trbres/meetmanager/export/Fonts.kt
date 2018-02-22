package nl.trbres.meetmanager.export

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import nl.trbres.meetmanager.Resources

/**
 * @author Ruben Schellekens
 */
object Fonts {

    val regular = FontFactory.getFont("Roboto-Regular", 9f)!!
    val small = FontFactory.getFont("Roboto-Regular", 7f)!!
    val bold = FontFactory.getFont("Roboto-Regular", 9f, Font.BOLD)!!
    val italic = FontFactory.getFont("Roboto-Regular", 9f, Font.ITALIC)!!
    val underline = FontFactory.getFont("Roboto-Regular", 9f, Font.UNDERLINE)!!
    val boldItalic = FontFactory.getFont("Roboto-Regular", 9f, Font.BOLDITALIC)!!

    init {
        FontFactory.register(Resources.FONT_ROBOTO, "Roboto-Regular")
    }
}