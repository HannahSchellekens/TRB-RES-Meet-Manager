package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
open class Lane(val number: Int, var swimmerId: Swimmer?) {

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