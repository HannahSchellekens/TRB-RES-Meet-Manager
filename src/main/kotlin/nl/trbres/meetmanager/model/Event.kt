package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * @author Ruben Schellekens
 */
data class Event(

        /**
         * The distance that will be swum.
         */
        val distance: Distance,

        /**
         * The stroke that will be swum.
         */
        val stroke: Stroke,

        /**
         * Who participate in the event.
         */
        val category: Category,

        /**
         * What age groups are allowed in the event.
         */
        val ages: MutableList<AgeGroup>
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

    override fun toString() = "${ages.first()[category]} ${ages.joinToString(",")} $distance $stroke"
}