package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.trbres.meetmanager.time.Time

/**
 * @author Ruben Schellekens
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
     * Checks if all heats in the event have their results filled in.
     */
    @JsonIgnore
    fun isFinished() = heats.all { it.isFinished() }

    /**
     * Collects all the swim results from the event, i.e. the swimmers with their swum times/statuses.
     *
     * The list has the result in order.
     */
    fun swimResults() = heats.flatMap { it.swimResults() }.sorted()

    override fun toString() = "${ages.first()[category]} ${ages.joinToString(",")} $distance $stroke"
}

/**
 * @author Ruben Schellekens
 */
data class SwimResult(val swimmer: Swimmer, val result: Time, val status: SpecialResult?) : Comparable<SwimResult> {

    override fun compareTo(other: SwimResult): Int {
        return result.compareTo(other.result)
    }
}