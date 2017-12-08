package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
data class Event(

        /**
         * The distance that will be swum.
         */
        val distance: Distance,

        /**
         * The stroke that will be swum.
         */
        val stroke: Stroke,

        /**
         * Who participate in the event.
         */
        val category: Category,

        /**
         * What age groups are allowed in the event.
         */
        val ages: MutableList<AgeGroup>
) {

    /**
     * All heats of the event.
     */
    val heats: MutableList<Heat> = ArrayList()
}