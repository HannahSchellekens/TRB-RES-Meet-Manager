package nl.trbres.meetmanager.util

import java.awt.Desktop
import java.net.URI

/**
 * Parses a string of format `n-m` to an int range `n..m`.
 */
fun String.toIntRange(): IntRange {
    val split = split("-")
    return split[0].toInt()..split[1].toInt()
}

/**
 * Opens the webbrowser to open the given url.
 */
fun String.openUrl() = Desktop.getDesktop().browse(URI(this))