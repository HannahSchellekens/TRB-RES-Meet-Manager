package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
data class Heat(

        /**
         * Maps lane numbers to swimmers.
         */
        val lanes: MutableMap<Int, Swimmer> = HashMap()
)