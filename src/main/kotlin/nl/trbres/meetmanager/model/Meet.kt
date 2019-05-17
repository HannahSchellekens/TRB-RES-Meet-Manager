package nl.trbres.meetmanager.model

import nl.trbres.meetmanager.time.Date
import nl.trbres.meetmanager.time.Time
import nl.trbres.meetmanager.util.indexRange

/**
 * @author Hannah Schellekens
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
         * The amount of hundredths somebody gets as penalty for getting a disqualification.
         */
        var penalty: Int = 0,

        /**
         * The collection of age groups.
         */
        var ageSet: AgeSet = AgeSet.SIMPLE,

        /**
         * The name of the meet organising party.
         */
        var organiser: String = "TRB-RES",

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

    fun collectEvents(events: List<Event>, convertTo: Int?): List<CollectedResult> {
        val collected = ArrayList<CollectedResult>()
        val eventResults = events.map { it to it.swimResults(convertTo) }.toMap()
        val swimmers = eventResults.values.asSequence()
                .flatten()
                .map { it.swimmer }
                .distinct()

        for (swimmer in swimmers) {
            val results = eventResults.entries
                    .map { (key, value) -> key to value.find { it.swimmer == swimmer } }.toMap()
            val total = results.values.filterNotNull()
                    .map { it.result }
                    .reduce { a, b -> a + b }
            collected.add(CollectedResult(swimmer, events, results, total))
        }

        // Rank people with more distances higher.
        val maxDistances = collected.asSequence()
                .map { collectedResult -> collectedResult.results.values.count { it != null && it.status == null } }
                .max() ?: 0

        val classes = arrayOfNulls<MutableList<CollectedResult>>(maxDistances + 1)
        for (i in classes.indices) {
            classes[i] = collected.asSequence()
                    .filter { collectedResult ->
                        collectedResult.results.values.count { it != null && (it.status == null || it.disqualification != null) } == i
                    }
                    .toMutableList()
        }
        classes.forEach { it!!.sort() }

        val result = ArrayList<CollectedResult>()
        for (i in classes.indices.reversed()) {
            result.addAll(classes[i]!!)
        }
        return result
    }
}

/**
 * @author Hannah Schellekens
 */
class CollectedResult(
        val swimmer: Swimmer,
        val events: List<Event>,
        val results: Map<Event, SwimResult?>,
        val total: Time
) : Comparable<CollectedResult> {

    override fun compareTo(other: CollectedResult): Int {
        return total.compareTo(other.total)
    }
}

/**
 * Generates all rank numbers/statusses in order.
 */
fun List<CollectedResult>.ranks() = mapIndexed { index, _ -> ((index + 1).toString()) }

/**
 * Generates all the swimmers that should be put on the event list (in order).
 */
fun List<CollectedResult>.swimmers() = map { it.swimmer }

/**
 * Generates all the names that should be put on the event list (in order).
 */
fun List<CollectedResult>.names() = map { it.swimmer.name }

/**
 * Generates all the birth years that should be put on the event list (in order).
 */
fun List<CollectedResult>.birthYears() = map { it.swimmer.birthYearDigits }

/**
 * Generates all club names in order.
 */
fun List<CollectedResult>.clubs() = map { it.swimmer.club?.name ?: "" }

/**
 * Generates all total times in order.
 */
fun List<CollectedResult>.totals() = map { it.total.toString() }

/**
 * Generates a list with maps that map event indices (in the events list) numbers to results.
 */
fun List<CollectedResult>.resultsTimes(events: List<Event>) = indexRange().map {
    val result = this[it]
    events.mapIndexed { index, event ->
        index to (result.results[event]?.result?.toString() ?: "")
    }.toMap()
}

/**
 * Generates a list with maps that map event indices (in the events list) numbers to ranks.
 */
fun List<CollectedResult>.resultRanks(events: List<Event>) = indexRange().map { rankindex ->
    val result = this[rankindex]
    events.mapIndexed { index, event ->
        index to ((event.swimResults().indexOfFirst { it.swimmer.id == result.swimmer.id } + 1).toString())
    }.toMap()
}