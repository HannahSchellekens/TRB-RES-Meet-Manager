package nl.trbres.meetmanager.export

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import nl.trbres.meetmanager.Resources

/**
 * @author Ruben Schellekens
 */
object Fonts {

    val robotoRegular = FontFactory.getFont("Roboto-Regular", 10f)!!
    val robotoSmall = FontFactory.getFont("Roboto-Regular", 8f)!!
    val robotoBold = FontFactory.getFont("Roboto-Regular", 10f, Font.BOLD)!!

    init {
        FontFactory.register(Resources.FONT_ROBOTO, "Roboto-Regular")
    }
}