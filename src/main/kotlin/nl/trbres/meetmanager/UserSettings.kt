package nl.trbres.meetmanager

import nl.trbres.meetmanager.util.APP_DATA_HOME
import nl.trbres.meetmanager.util.createAppData
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.*

/**
 * @author Ruben Schellekens
 */
object UserSettings {

    object Key {

        val lastDirectory = "last-directory"
        val lastExportDirectory = "last-export-directory"
    }

    private val filePath = "$APP_DATA_HOME/user.settings"
    private val properties = Properties()

    init {
        createAppData()

        val propertiesFile = File(filePath)
        if (!propertiesFile.exists()) {
            writePropertiesFile()
        }

        loadPropertiesFile()
    }

    /**
     * Saves value `value` in the user settings.
     */
    fun save(key: String, value: String) {
        properties.setProperty(key, value)
        writePropertiesFile()
    }

    /**
     * Loads the value of a given `key` from the user setting.
     */
    fun load(key: String): String? {
        return properties.getProperty(key)
    }

    private fun loadPropertiesFile() {
        val input = FileInputStream(filePath)
        input.use {
            properties.load(input)
        }
    }

    private fun writePropertiesFile() {
        val output = FileWriter(filePath)
        output.use {
            properties.store(output, null)
        }
    }

    operator fun get(key: String) = load(key)
    operator fun set(key: String, value: String) = save(key, value)
}