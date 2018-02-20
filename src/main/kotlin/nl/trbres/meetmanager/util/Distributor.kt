package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.model.Event
import nl.trbres.meetmanager.model.Heat
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.model.Swimmer
import kotlin.math.max

/**
 * @author Ruben Schellekens
 */
open class Distributor(private val meet: Meet) {

    private fun laneOrder(): List<Int> {
        val lanes = meet.lanes
        val middleLane = (lanes.start + lanes.size) / 2
        val left = (middleLane downTo lanes.start).toList()
        val right = ((middleLane + 1)..lanes.endInclusive).toList()

        val result = ArrayList<Int>()
        for (i in 0 until max(left.size, right.size)) {
            result += left[i]
            if (i in right.indices) {
                result += right[i]
            }
        }

        return result
    }

    /**
     * Distributes all swimmers over the meet, creating heats in the process.
     */
    fun distribute() {
        // Initialise
        val swimmers = meet.swimmers
        val availableLanes = laneOrder()
        val events = meet.events.filter { it.distance.times == 1 }
        val mapping = HashMap<Event, MutableList<Swimmer>>()
        for (event in events) {
            mapping[event] = ArrayList()
        }

        // Filter swimmers
        for (event in events) {
            swimmers.asSequence()
                    .filter { it.age in event.ages && it.category == event.category }
                    .forEach { mapping[event]!!.add(it) }
        }

        // Create heats
        val lanes = meet.lanes
        for (event in events) {
            val addedHeats = ArrayList<Heat>()
            val eventSwimmers = mapping[event]!!.toMutableList()
            eventSwimmers.shuffle()
            eventSwimmers.chunked(lanes.endInclusive - lanes.start + 1).reversed().forEach {
                val heat = Heat()
                it.zip(availableLanes).forEach {
                    heat.lanes[it.second] = it.first
                }
                event.heats.add(heat)
                addedHeats += heat
            }

            if (addedHeats.size > 1) {
                adjustHeats(addedHeats.first(), addedHeats[1])
            }
        }
    }

    /**
     * Redistributes the swimmers of the first two heats to ensure that each heat has at least 3 swimmers in the
     * middle lanes.
     */
    fun adjustHeats(firstHeat: Heat, secondHeat: Heat) {
        val lanes = laneOrder()
        val minSwimmers = 2 + if (lanes.size > 4) 1 else 0

        var toAdd = firstHeat.lanes.values.size
        var toRemove = lanes.lastIndex

        while (firstHeat.lanes.values.size < minSwimmers) {
            val takenSwimmer = secondHeat.lanes[lanes[toRemove]]
            secondHeat.lanes.remove(lanes[toRemove--])
            firstHeat.lanes[lanes[toAdd++]] = takenSwimmer!!
        }
    }
}

val Meet.distributor
    get() = Distributor(this)