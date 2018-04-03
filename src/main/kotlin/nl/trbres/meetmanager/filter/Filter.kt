package nl.trbres.meetmanager.filter

/**
 * @author Ruben Schellekens
 */
interface Filter<in T> {

    fun filter(item: T): Boolean
}