package nl.trbres.meetmanager.model

import java.time.LocalDate

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
        var date: LocalDate,

        /**
         * The lane numbers that are in use for the meet.
         */
        var lanes: IntRange,

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