package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
open class Lane(val number: Int, var swimmerId: UUID?) {

    companion object {

        @JvmStatic
        fun emptyLane(number: Int) = Lane(number, null)
    }

    /**
     * Whether the lane is empty or not.
     */
    val empty
        get() = swimmerId == null
}