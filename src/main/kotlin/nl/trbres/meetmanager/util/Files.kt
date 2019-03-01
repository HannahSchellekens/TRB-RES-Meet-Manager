package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Meet

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