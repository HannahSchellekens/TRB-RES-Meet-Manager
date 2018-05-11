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
     * Converts the date to a [LocalDate].
     */
    fun toLocalDate() = LocalDate.of(year, month, day)!!

    /**
     * Converts to a dutch readable name, example: `6 mei 2018`.
     */
    fun toDutchName(): String {
        val month = when (month) {
            1 -> "januari"
            2 -> "februari"
            3 -> "maart"
            4 -> "april"
            5 -> "mei"
            6 -> "juni"
            7 -> "juli"
            8 -> "augustus"
            9 -> "september"
            10 -> "oktober"
            11 -> "november"
            12 -> "december"
            else -> error("Illegal month: $month")
        }

        return "$day $month $year"
    }

    /**
     * See [iso8601].
     */
    override fun toString() = iso8601()
}

/**
 * Converts a local date to a [Date].
 */
fun LocalDate.toDate() = Date(year, monthValue, dayOfMonth)