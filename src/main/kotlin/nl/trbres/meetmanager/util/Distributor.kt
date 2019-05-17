package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.model.*
import java.util.*
import kotlin.math.floor

/**
 * @author Hannah Schellekens
 */
open class Distributor(private val meet: Meet) {

    private fun laneOrder(): List<Int> {
        val lanes = meet.lanes
        val pivot = floor((lanes.endInclusive + lanes.start) / 2.0).toInt()

        val left = ArrayDeque((lanes.start until pivot).reversed().toList())
        val right = ArrayDeque(((pivot + 1)..lanes.endInclusive).toList())

        return mutableListOf(pivot).also { result ->
            while (right.isNotEmpty() || left.isNotEmpty()) {
                if (right.isNotEmpty()) {
                    result.add(right.pop())
                }
                if (left.isNotEmpty()) {
                    result.add(left.pop())
                }
            }
        }
    }

    /**
     * Distributes all swimmers over the meet, creating heats in the process.
     */
    fun distribute() {
        // Initialise
        val swimmers = meet.swimmers
        val availableLanes = laneOrder()
        val events = meet.events
        val mapping = HashMap<Event, MutableList<Swimmer>>()
        for (event in events) {
            mapping[event] = ArrayList()
        }

        // Filter swimmers
        for (event in events) {
            swimmers.asSequence()
                    .filter { swimmer ->
                        swimmer.category == event.category &&
                                event.ages.any { swimmer.age.isJointCategory(it) } &&
                                swimmer is Relay == event.isRelay()
                    }
                    .forEach { mapping[event]!!.add(it) }
        }

        // Create heats
        val lanes = meet.lanes
        for (event in events) {
            val addedHeats = ArrayList<Heat>()
            val eventSwimmers = mapping[event]!!.toMutableList()
            eventSwimmers.shuffle()
            eventSwimmers.chunked(lanes.endInclusive - lanes.start + 1).reversed().forEach { heatSwimmers ->
                val heat = Heat()
                heatSwimmers.zip(availableLanes).forEach {
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