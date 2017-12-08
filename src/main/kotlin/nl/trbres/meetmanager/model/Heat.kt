package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
data class Heat(

        /**
         * Maps lane numbers to swimmer UUIDs.
         */
        val lanes: MutableMap<Int, UUID> = HashMap()
)