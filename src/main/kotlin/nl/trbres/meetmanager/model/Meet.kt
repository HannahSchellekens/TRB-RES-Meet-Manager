package nl.trbres.meetmanager.model

import nl.trbres.meetmanager.time.Date
import nl.trbres.meetmanager.time.Time

/**
 * @author Ruben Schellekens
 */
data class Meet(

        /**
         * The name of the swim meet.
         */
        var name: String,

        /**
         * The date of the swim meet.
         */
        var date: Date,

        /**
         * The lane numbers that are in use for the meet.
         */
        var lanes: IntRange,

        /**
         * The name of the location where the meet is being held.
         */
        var location: String,

        /**
         * All the events of the meet.
         */
        val events: MutableList<Event> = ArrayList(),

        /**
         * All clubs that participate in the meet.
         */
        val clubs: MutableList<Club> = ArrayList(),

        /**
         * All swimmers that participate in the meet.
         */
        val swimmers: MutableList<Swimmer> = ArrayList()
) {

    fun collectEvents(events: List<Event>): List<CollectedResult> {
        val collected = ArrayList<CollectedResult>()
        val eventResults = events.map { it to it.swimResults() }.toMap()
        val swimmers = eventResults.values.flatMap { it }.map { it.swimmer }.distinct()

        for (swimmer in swimmers) {
            val results = eventResults.entries.map { (key, value) -> key to value.find { it.swimmer == swimmer } }.toMap()
            val total = results.values.filterNotNull().map { it.result }.reduce { a, b -> a + b }
            collected.add(CollectedResult(swimmer, events, results, total))
        }

        return collected.sorted()
    }
}

/**
 * @author Ruben Schellekens
 */
class CollectedResult(
        val swimmer: Swimmer,
        val events: List<Event>,
        val results: Map<Event, SwimResult?>,
        val total: Time
) : Comparable<CollectedResult> {

    override fun compareTo(other: CollectedResult): Int {
        val comparisonValue = if (results.none {
            val value = it.value ?: return@none true
            value.result.isZero()
        }) {
            Time(9999, 9999, 9999, 9999)
        } else total
        return comparisonValue.compareTo(other.total)
    }
}