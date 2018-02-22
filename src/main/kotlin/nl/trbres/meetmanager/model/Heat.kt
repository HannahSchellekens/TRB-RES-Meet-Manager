package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.trbres.meetmanager.State
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
        val statusses: MutableMap<Int, SpecialResult> = HashMap(),

        /**
         * Map lane numbers to disqualifications.
         */
        val disqualifications: MutableMap<Int, Disqualification> = HashMap()
) {

    /**
     * Checks if all times are entered.
     */
    @JsonIgnore
    fun isFinished() = lanes.keys.all { it in results.keys || it in statusses.keys } && results.values.none { it == Time(0, 0) }

    /**
     * Collects all the swim results from the heat, i.e. the swimmers with their swum times/statuses.
     */
    fun swimResults(timeFactor: Float) = lanes.keys.mapNotNull {
        val swimmer = lanes[it] ?: return@mapNotNull null
        var time = results[it]
        var status = statusses[it]
        val disqualification = disqualifications[it]

        if (swimmer.name == "<Leeg>") {
            return@mapNotNull null
        }

        if (time == null || time.isZero()) {
            status = SpecialResult(SpecialResultType.DID_NOT_START)
            time = Time.INVALID
        }

        // Add penalty.
        if (disqualification != null) {
            time += Time(State.meet!!.penalty)
        }

        SwimResult(swimmer, time * timeFactor, status, disqualification)
    }
}
