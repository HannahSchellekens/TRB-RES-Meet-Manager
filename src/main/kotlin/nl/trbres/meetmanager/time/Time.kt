package nl.trbres.meetmanager.time

import java.time.LocalTime

/**
 * @author Ruben Schellekens
 */
data class Time(var hours: Int, var minutes: Int, var seconds: Int, var hundreths: Int) {

    constructor(minutes: Int, seconds: Int, hundreths: Int) : this(0, minutes, seconds, hundreths)

    constructor(seconds: Int, hundreths: Int) : this(0, 0, seconds, hundreths)

    constructor() : this(0, 0, 0, 0) {
        val now = LocalTime.now()
        hours = now.hour
        minutes = now.minute
        seconds = now.second
        hundreths = (System.currentTimeMillis() % 1000 / 10).toInt()
    }

    override fun toString() = if (hours > 0) {
        String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, hundreths)
    }
    else if (minutes > 0) {
        String.format("%02d:%02d.%02d", minutes, seconds, hundreths)
    }
    else {
        String.format("%02d.%02d", seconds, hundreths)
    }
}