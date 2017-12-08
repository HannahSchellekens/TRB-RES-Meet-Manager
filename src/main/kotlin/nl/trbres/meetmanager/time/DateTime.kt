package nl.trbres.meetmanager.time

/**
 * @author Ruben Schellekens
 */
data class DateTime(val date: Date, val time: Time) {

    constructor() : this(Date(), Time())

    fun year() = date.year
    fun month() = date.month
    fun day() = date.day
    fun hour() = time.hours
    fun minute() = time.minutes
    fun second() = time.seconds
    fun hundreth() = time.hundreths

    override fun toString() = String.format(
            "%04d-%02d-%02d %02d:%02d:%02d.%02d",
            year(), month(), day(), hour(), minute(), second(), hundreth()
    )
}