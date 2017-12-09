package nl.trbres.meetmanager.util

/**
 * @author Ruben Schellekens
 */
open class Reference<T>(var value: T?) {

    override fun toString() = value.toString()
}