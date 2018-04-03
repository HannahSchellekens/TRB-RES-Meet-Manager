package nl.trbres.meetmanager.filter

import nl.trbres.meetmanager.model.AgeGroup
import nl.trbres.meetmanager.model.Swimmer

/**
 * @author Ruben Schellekens
 */
open class AgeGroupFilter(val group: AgeGroup) : SwimmerFilter {

    override fun filter(item: Swimmer) = item.age == group
}