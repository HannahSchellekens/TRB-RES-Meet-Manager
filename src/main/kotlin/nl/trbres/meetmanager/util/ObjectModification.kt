package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.model.Swimmer

/**
 * Deletes the club from:
 * - The list of clubs.
 * - Unbinds the club from all swimmers with that club.
 * - Unbinds the club from all swimmers in all heats.
 */
fun Club.nestedDelete() {
    val meet = State.meet ?: return
    meet.clubs.remove(this)
    meet.swimmers
            .filter { it.club == this@nestedDelete }
            .forEach { it.club = null }
    meet.events
            .flatMap { it.heats }
            .flatMap { it.lanes.values }
            .filter { it.club == this@nestedDelete }
            .forEach { it.club = null }
}

/**
 * Updates the status with the club in all places except the main club list.
 */
fun Club.nestedUpdate() {
    val meet = State.meet ?: return
    meet.swimmers
            .filter { it.club.isNotNull() && it.club!!.id == this@nestedUpdate.id }
            .forEach { it.club = this }
    meet.events
            .flatMap { it.heats }
            .flatMap { it.lanes.values }
            .filter { it.club.isNotNull() && it.club!!.id == this@nestedUpdate.id }
            .forEach { it.club = this }
}

/**
 * Deletes a swimmer:
 * - Removes from swimmer list.
 * - Removes from all heats where the swimmer participates.
 */
fun Swimmer.nestedDelete() {
    val meet = State.meet ?: return
    meet.swimmers.remove(this)
    meet.events
            .flatMap { it.heats }
            .forEach { heat ->
                val lane = heat.lanes.getKey(this@nestedDelete) ?: return@forEach
                heat.lanes.remove(lane)
                heat.results.remove(lane)
                heat.statusses.remove(lane)
            }
    meet.events
            .flatMap { it.heats }
            .flatMap { it.lanes.values }
            .mapNotNull { it as? Relay }
            .forEach { relay ->
                relay.members.remove(this@nestedDelete)
            }
}

/**
 * Updates all the data of the swimmer in the heats.
 */
fun Swimmer.nestedUpdate() {
    val meet = State.meet ?: return
    meet.events
            .flatMap { it.heats }
            .flatMap { it.lanes.values }
            .filter { it.id == this@nestedUpdate.id && it !== this@nestedUpdate}
            .forEach {
                it.name = this@nestedUpdate.name
                it.age = this@nestedUpdate.age
                it.category = this@nestedUpdate.category
                it.club = this@nestedUpdate.club
                it.birthYear = this@nestedUpdate.birthYear

                if (it is Relay && this@nestedUpdate is Relay) {
                    it.members.clear()
                    it.members.addAll(this@nestedUpdate.members)
                }
            }
}