package nl.trbres.meetmanager.model

import nl.trbres.meetmanager.time.Time

/**
 * @author Ruben Schellekens
 */
data class Heat(

        /**
         * Maps lane numbers to swimmers.
         */
        val lanes: MutableMap<Int, Swimmer> = HashMap(),

        /**
         * Maps lane numbers to the swum times.
         */
        val results: MutableMap<Int, Time> = HashMap(),

        /**
         * Map lane numbers to special result statusses.
         */
        val statusses: MutableMap<Int, SpecialResult> = HashMap()
) {

    /**
     * Checks if all times are entered.
     */
    fun isFinished() = lanes.keys.all { it in results.keys || it in statusses.keys }
}