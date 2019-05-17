package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.trbres.meetmanager.time.Time

/**
 * @author Hannah Schellekens
 */
data class Event(

        /**
         * The distance that will be swum.
         */
        var distance: Distance,

        /**
         * The stroke that will be swum.
         */
        var stroke: Stroke,

        /**
         * Who participate in the event.
         */
        var category: Category,

        /**
         * What age groups are allowed in the event.
         */
        var ages: MutableList<AgeGroup>
) {

    /**
     * All heats of the event.
     */
    val heats: MutableList<Heat> = ArrayList()

    /**
     * `true` if the event is a relay, `false` if it is an individual event.
     */
    @JsonIgnore
    fun isRelay() = distance.times > 1

    /**
     * Checks if all heats in the event have their results filled in.
     */
    @JsonIgnore
    fun isFinished() = heats.all { it.isFinished() }

    /**
     * Collects all the swim results from the event, i.e. the swimmers with their swum times/statuses.
     *
     * The list has the result in order.
     */
    fun swimResults(convertTo: Int? = null): List<SwimResult> {
        val results = heats.flatMap {
            val factor = if (convertTo == null) 1f else convertTo.toFloat() / distance.metres.toFloat()
            it.swimResults(factor)
        }

        val regular = results.asSequence()
                .filter { it.status == null }
                .sorted()
                .toList()
        val disqualified = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DISQUALIFIED }
                .sortedBy { it.result }
                .toList()
        val didNotStart = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DID_NOT_START }
                .sortedBy { it.swimmer.name }
                .toList()
        val didNotStartWithoutCancellation = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DID_NOT_START_WITHOUT_CANCELLATION }
                .sortedBy { it.swimmer.name }
                .toList()

        return regular + disqualified + didNotStart + didNotStartWithoutCancellation
    }

    override fun toString() = "${ages.first()[category]} ${ages.joinToString(",")}, $distance $stroke"

    /**
     * @author Hannah Schellekens
     */
    enum class Options {

    }
}

/**
 * @author Hannah Schellekens
 */
data class SwimResult(
        val swimmer: Swimmer,
        val result: Time,
        val status: SpecialResult?,
        val disqualification: Disqualification?
) : Comparable<SwimResult> {

    override fun compareTo(other: SwimResult): Int {
        return result.compareTo(other.result)
    }
}