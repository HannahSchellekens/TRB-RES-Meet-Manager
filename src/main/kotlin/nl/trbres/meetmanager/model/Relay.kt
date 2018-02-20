package nl.trbres.meetmanager.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Ruben Schellekens
 */
open class Relay(name: String, age: AgeGroup, category: Category, club: Club?, id: UUID = UUID.randomUUID())
    : Swimmer(name, age, category, club, id) {

    /**
     * A list containing all the swimmers who are part of the relay team (in order).
     */
    val members: MutableList<Swimmer> = ArrayList()
}