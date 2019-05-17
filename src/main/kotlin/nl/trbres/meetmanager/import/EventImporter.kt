package nl.trbres.meetmanager.import

import nl.trbres.meetmanager.model.*

/**
 * @author Hannah Schellekens
 */
open class EventImporter(val input: String, val meet: Meet) {

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

                val distance = entry[0].parseDistance() ?: return@mapNotNull null
                val stroke = Stroke.values().find { stroke -> stroke.name == entry[1] } ?: return@mapNotNull null
                val ageGroup = try {
                    meet.ageSet.ages.find { age -> age.id == entry[2] } ?: return@mapNotNull null
                }
                catch (e: IllegalArgumentException) {
                    return@mapNotNull null
                }
                val category = when (entry[3]) {
                    "m" -> Category.MALE
                    "v" -> Category.FEMALE
                    else -> Category.MIX
                }

                // Import metric.
                val metric = if (entry.size >= 5) {
                    Event.Metric.valueOf(entry[4])
                }
                else Event.Metric.TIME

                // Import modifiers
                val modifiers = if (entry.size >= 6) {
                    entry[5].split(",").map { input -> Event.Modifier.valueOf(input) }.toSet()
                }
                else emptySet()

                Event(distance, stroke, category, mutableListOf(ageGroup), metric, modifiers)
            }

    private fun String.parseDistance(): Distance? {
        val numbers = split('x').mapNotNull { it.toIntOrNull() }
        if (numbers.size != 2) return null
        return Distance(numbers.last(), numbers.first())
    }
}