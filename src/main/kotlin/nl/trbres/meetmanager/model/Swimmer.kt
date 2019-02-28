package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
open class Swimmer(var name: String, var age: AgeGroup, var category: Category, var club: Club?, birthYear: Int? = null, var id: UUID = UUID.randomUUID()) {

    var birthYear: Int? = birthYear
        set(value) {
            field = if (value ?: 0 < 1900) null else value
        }

    /**
     * Get the last two digits of the swimmer's birth year. Returns an empty string when the swimmer has no
     * recorded birth year.
     */
    val birthYearDigits: String
        @JsonIgnore
        get() = birthYear?.rem(100)?.let { "%02d".format(it) } ?: ""

    /**
     * Get the name of the swimmer with the last two digits of the birth year appended if they are available.
     */
    val nameWithBirthYear: String
        @JsonIgnore
        get() = "$name $birthYearDigits".trimEnd()

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