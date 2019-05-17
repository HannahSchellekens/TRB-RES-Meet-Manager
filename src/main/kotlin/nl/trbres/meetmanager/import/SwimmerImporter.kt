package nl.trbres.meetmanager.import

import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.model.Swimmer

/**
 * @author Hannah Schellekens
 */
open class SwimmerImporter(val input: String, val meet: Meet) {

    /**
     * Map mapping all club names to their objects.
     */
    private val clubs = HashMap<String, Club>().apply {
        for (club in meet.clubs) {
            this[club.name] = club
        }
    }

    /**
     * Converts the input to a list of swimmers.
     */
    open fun import() = input.trim().split("\n")
            .mapNotNull {
                val entry = it.split("\t")
                if (entry.size < 4) {
                    return@mapNotNull null
                }

                val category = if (entry[0] == "m") Category.MALE else Category.FEMALE
                val name = entry[1]
                val ageGroup = try {
                    meet.ageSet.ages.find { age -> age.id == entry[2] } ?: return@mapNotNull null
                }
                catch (e: IllegalArgumentException) {
                    return@mapNotNull null
                }
                val club = clubs[entry[3]]

                val birthYear = entry.getOrNull(4)?.toIntOrNull()

                Swimmer(name, ageGroup, category, club, birthYear)
            }
}