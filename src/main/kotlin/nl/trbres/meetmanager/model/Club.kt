package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

/**
 * @author Ruben Schellekens
 */
data class Club(var name: String, val id: UUID = UUID.randomUUID()) {

    companion object {

        @JvmField
        val NO_CLUB = Club("<Geen>")
    }

    @JsonIgnore
    fun isNoClub() = name == "<Geen>"

    /**
     * Returns `null` for [NO_CLUB], or the club object otherwise.
     */
    fun mapToClub() = if (this == NO_CLUB) null else this

    override fun toString() = name
}