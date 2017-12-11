package nl.trbres.meetmanager.model

import nl.trbres.meetmanager.time.Date

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
)