package nl.trbres.meetmanager.model

/**
 * @author Hannah Schellekens
 */
open class Lane(val number: Int, var swimmer: Swimmer?) {

    companion object {

        @JvmStatic
        fun emptyLane(number: Int) = Lane(number, null)
    }

    /**
     * Whether the lane is empty or not.
     */
    val empty
        get() = swimmer == null

    override fun toString() = "$number. $swimmer"
}