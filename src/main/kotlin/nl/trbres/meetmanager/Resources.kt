package nl.trbres.meetmanager

import nl.trbres.meetmanager.util.APP_DATA_HOME
import nl.trbres.meetmanager.util.createAppData
import java.io.File
import java.io.FileOutputStream

/**
 * @author Ruben Schellekens
 */
object Resources {

    val FONT_ROBOTO = "$APP_DATA_HOME/Roboto-Regular.ttf"

    init {
        createAppData()

        extract("/fonts/Roboto-Regular.ttf", FONT_ROBOTO)
    }

    /**
     * Extracts the resources with path `classpath` to file `file`.
     */
    fun extract(classpath: String, filePath: String) {
        val output = FileOutputStream(File(filePath))
        Resources::class.java.getResourceAsStream(classpath).use { input ->
            output.use {
                var byte = input.read()
                while (byte != -1) {
                    output.write(byte)
                    byte = input.read()
                }
            }
        }
    }
}