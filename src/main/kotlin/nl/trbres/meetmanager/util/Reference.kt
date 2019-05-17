package nl.trbres.meetmanager.util

/**
 * @author Hannah Schellekens
 */
open class Reference<T>(var value: T?) {

    override fun toString() = value.toString()
}