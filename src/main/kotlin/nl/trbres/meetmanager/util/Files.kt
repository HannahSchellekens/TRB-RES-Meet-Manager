package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.UserSettings
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Meet
import java.io.File

/**
 * Get the proposed file name of a schedule booklet.
 *
 * @param club
 *          The club whose name must appear in the file name, `null` to ignore.
 */
fun fileNameOfSchedule(meet: Meet, club: Club? = null): String {
    val clubName = club?.fileNameFriendlyString()
    val clubSuffix = if (clubName == null) "" else "_$clubName"
    val meetName = meet.fileNameFriendlyString()
    return "Schedule_$meetName$clubSuffix.pdf"
}

object RecentFiles {

    /**
     * Get called whenever the recent files change.
     */
    val callbacks = ArrayList<(List<File>) -> Unit>()

    /**
     * Get the 10 latest used meet files.
     */
    fun recentfiles(): List<File> {
        return (0..9).asSequence()
                .mapNotNull { UserSettings["ugly-recent-file-$it"] }
                .filter { it.isNotBlank() }
                .map { File(it) }
                .toList()
    }

    /**
     * Adds the given file to the recent file list (see [recentfiles]).
     */
    fun pushRecentFile(file: File) {
        (listOf(file) + (recentfiles())).distinct().take(10).forEachIndexed { index, theFile ->
            UserSettings["ugly-recent-file-$index"] = theFile.absolutePath
        }
        val files = recentfiles()
        callbacks.forEach { it(files) }
    }
}