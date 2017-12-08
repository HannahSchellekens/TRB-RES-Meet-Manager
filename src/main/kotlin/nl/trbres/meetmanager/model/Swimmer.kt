package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
data class Swimmer(var name: String, var age: AgeGroup, var category: Category, var club: Club?, val id: UUID = UUID.randomUUID())