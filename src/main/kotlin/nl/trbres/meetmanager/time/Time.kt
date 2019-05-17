package nl.trbres.meetmanager.time

import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.util.StringConverter
import nl.trbres.meetmanager.model.Event
import java.time.LocalTime

/**
 * @author Hannah Schellekens
 */
data class Time(var hours: Int, var minutes: Int, var seconds: Int, var hundreths: Int) : Comparable<Time> {

    companion object {

        val INVALID = Time(0, 0)
    }

    constructor(minutes: Int, seconds: Int, hundreths: Int) : this(0, minutes, seconds, hundreths)

    constructor(seconds: Int, hundreths: Int) : this(0, 0, seconds, hundreths)

    constructor(hundreths: Int) : this(0, 0, 0, hundreths)

    constructor(hundreths: Long) : this(0, 0, 0, hundreths.toInt())

    constructor() : this(0, 0, 0, 0) {
        val now = LocalTime.now()
        hours = now.hour
        minutes = now.minute
        seconds = now.second
        hundreths = (System.currentTimeMillis() % 1000 / 10).toInt()
    }

    init {
        reduceHunderths()
        reduceSeconds()
        reduceMinutes()
    }

    /**
     * Converts the timestamp to hunderths of a second.
     */
    fun toHundreths() = (((hours * 60L) + minutes) * 60L + seconds) * 100L + hundreths

    /**
     * Checks whether the time equals zero.
     */
    @JsonIgnore
    fun isZero() = hours == 0 && minutes == 0 && seconds == 0 && hundreths == 0

    private fun reduceHunderths() {
        if (hundreths > 99) {
            seconds += (hundreths / 100)
            hundreths %= 100
        }
    }

    private fun reduceSeconds() {
        if (seconds > 59) {
            minutes += (seconds / 60)
            seconds %= 60
        }
    }

    private fun reduceMinutes() {
        if (minutes > 59) {
            hours += (minutes / 60)
            minutes %= 60
        }
    }

    operator fun times(factor: Float) = Time((toHundreths() * factor).toInt())

    operator fun plus(other: Time) = Time(
            hours + other.hours,
            minutes + other.minutes,
            seconds + other.seconds,
            hundreths + other.hundreths
    )

    override fun compareTo(other: Time): Int {
        val hundreths = toHundreths()
        val comparisonValue = if (hundreths == 0L) Integer.MAX_VALUE else hundreths.toInt()
        return comparisonValue.compareTo(other.toHundreths())
    }

    override fun toString() = if (this == INVALID) {
        "NT"
    }
    else if (hours > 0) {
        String.format("%2d:%02d:%02d.%02d", hours, minutes, seconds, hundreths)
    }
    else if (minutes > 0) {
        String.format("%d:%02d.%02d", minutes, seconds, hundreths)
    }
    else {
        String.format("%2d.%02d", seconds, hundreths)
    }

    fun toMetresString() = if (this == INVALID) {
        "ND"
    }
    else String.format("%.1fm", toHundreths() / 100f)
}

/**
 * Converts strings to [Time] objects and vice versa.
 *
 * @author Hannah Schellekens
 */
open class TimeConverter(val metric: Event.Metric) : StringConverter<Time>() {

    override fun toString(time: Time?) = when (metric) {
        Event.Metric.TIME -> time?.toString() ?: ""
        Event.Metric.DISTANCE -> time?.toMetresString() ?: ""
    }

    override fun fromString(string: String?): Time {
        if (metric == Event.Metric.DISTANCE) {
            val metres = string?.toFloatOrNull() ?: return Time(0, 0)
            val hundreths = (metres * 100).toInt()
            return Time(hundreths)
        }

        val time = string?.replace(Regex("[^\\d]"), "") ?: return Time(0, 0)
        return when (time.length) {
            1 -> Time(0, time(0..0))
            2 -> Time(0, time(0..1))
            3 -> Time(time(0..0), time(1..2))
            4 -> Time(time(0..1), time(2..3))
            5 -> Time(time(0..0), time(1..2), time(3..4))
            6 -> Time(time(0..1), time(2..3), time(4..5))
            7 -> Time(time(0..0), time(1..2), time(3..4), time(5..6))
            8 -> Time(time(0..1), time(2..3), time(4..5), time(6..7))
            else -> Time(0, 0)
        }
    }

    private operator fun String.invoke(range: IntRange) = slice(range).toInt()
}