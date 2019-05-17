package nl.trbres.meetmanager.filter

/**
 * @author Hannah Schellekens
 */
interface Filter<in T> {

    fun filter(item: T): Boolean
}