package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
open class Swimmer(var name: String, var age: AgeGroup, var category: Category, var club: Club?, val id: UUID = UUID.randomUUID()) {

    override fun toString() = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Swimmer) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}