package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
data class Distance(

        /**
         * How many metres must be swum by each swimmer.
         */
        val metres: Int,

        /**
         * The amount of times that has to be swum.
         *
         * 1 for individual, and 4 for a typical relay.
         */
        val times: Int
) {

    companion object {

        @JvmField val INDIVIDUAL_25 = Distance(25, 1)
        @JvmField val INDIVIDUAL_50 = Distance(50, 1)
        @JvmField val INDIVIDUAL_100 = Distance(100, 1)
        @JvmField val INDIVIDUAL_200 = Distance(200, 1)
        @JvmField val INDIVIDUAL_400 = Distance(400, 1)
        @JvmField val INDIVIDUAL_800 = Distance(800, 1)
        @JvmField val INDIVIDUAL_1500 = Distance(1500, 1)
        @JvmField val MEDLEY_4x25 = Distance(25, 4)
        @JvmField val MEDLEY_4x50 = Distance(50, 4)
        @JvmField val MEDLEY_4x100 = Distance(100, 4)
        @JvmField val MEDLEY_4x200 = Distance(200, 4)

        @JvmField val DEFAULTS = listOf(
                INDIVIDUAL_25, INDIVIDUAL_50, INDIVIDUAL_100, INDIVIDUAL_200,
                INDIVIDUAL_400, INDIVIDUAL_800, INDIVIDUAL_1500,
                MEDLEY_4x25, MEDLEY_4x50, MEDLEY_4x100, MEDLEY_4x200
        )

        /**
         * Maps all [DEFAULTS] to a human readable name.
         */
        @JvmField val DEFAULT_NAMES = mapOf(
                INDIVIDUAL_25 to "Individueel 25m",
                INDIVIDUAL_50 to "Individueel 50m",
                INDIVIDUAL_100 to "Individueel 100m",
                INDIVIDUAL_200 to "Individueel 200m",
                INDIVIDUAL_400 to "Individueel 400m",
                INDIVIDUAL_800 to "Individueel 800m",
                INDIVIDUAL_1500 to "Individueel 1500m",
                MEDLEY_4x25 to "Estafette 4x25m",
                MEDLEY_4x50 to "Estafette 4x50m",
                MEDLEY_4x100 to "Estafette 4x100m",
                MEDLEY_4x200 to "Estafette 4x200m"
        )
    }

    /**
     * The total name of the distance.
     *
     * Examples: `50m`, `400m`, `4x50m`.
     */
    val title = when (times) {
        1 -> "${metres}m"
        else -> "${times}x${metres}m"
    }

    init {
        require(times > 0) { "Amount of times must be positive, got $times" }
    }

    override fun toString() = title
}