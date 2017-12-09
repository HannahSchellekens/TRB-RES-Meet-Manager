package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
data class Club(val name: String, val id: UUID = UUID.randomUUID()) {

    override fun toString() = name
}