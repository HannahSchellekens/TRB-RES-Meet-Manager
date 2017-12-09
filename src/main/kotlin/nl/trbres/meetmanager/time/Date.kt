package nl.trbres.meetmanager.time

import java.time.LocalDate

/**
 * @author Ruben Schellekens
 */
data class Date(var year: Int, var month: Int, var day: Int) {

    /**
     * Creates the current date.
     */
    constructor() : this(0, 0, 0) {
        val now = LocalDate.now()
        year = now.year
        month = now.monthValue
        day = now.dayOfMonth
    }

    /**
     * Prints the date in ISO 8601 format (YYYY-MM-DD).
     */
    fun iso8601() = String.format("%04d-%02d-%02d", year, month, day)

    /**
     * See [iso8601].
     */
    override fun toString() = iso8601()
}

/**
 * Converts a local date to a [Date].
 */
fun LocalDate.toDate() = Date(year, monthValue, dayOfMonth)