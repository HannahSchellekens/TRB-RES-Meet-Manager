package nl.trbres.meetmanager.filter

import nl.trbres.meetmanager.model.Swimmer

/**
 * @author Hannah Schellekens
 */
interface SwimmerFilter : Filter<Swimmer> {

    companion object {

        val NO_FILTER = object : SwimmerFilter {
            override fun filter(item: Swimmer) = true
        }
    }
}