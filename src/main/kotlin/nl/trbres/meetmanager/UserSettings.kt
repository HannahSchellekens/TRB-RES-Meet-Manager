package nl.trbres.meetmanager

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
    }

    private val settingsPath = System.getProperty("user.home") + "/.trbmeet"
    private val filePath = "$settingsPath/user.settings"
    private val properties = Properties()

    init {
        val settingsDir = File(settingsPath)
        if (!settingsDir.exists()) {
            settingsDir.mkdir()
        }

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