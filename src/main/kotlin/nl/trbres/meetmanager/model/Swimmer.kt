package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.*

/**
 * @author Ruben Schellekens
 */
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT, property = "implementation")
@JsonSubTypes(
        JsonSubTypes.Type(value = Swimmer::class, name = "swimmer"),
        JsonSubTypes.Type(value = Relay::class, name = "relay")
)
@JsonTypeName("swimmer")
open class Swimmer(var name: String, var age: AgeGroup, var category: Category, var club: Club?, var id: UUID = UUID.randomUUID()) {

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