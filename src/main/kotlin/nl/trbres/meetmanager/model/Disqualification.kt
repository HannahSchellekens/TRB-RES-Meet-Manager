package nl.trbres.meetmanager.model

import java.util.*

/**
 * @author Ruben Schellekens
 */
class Disqualification(val code: DisqualificationCode, val swimmerId: UUID?) {

    fun fullMessage(swimmer: Swimmer? = null): String {
        val swimmerSuffix = if (swimmer != null) {
            " [${swimmer.name}]"
        }
        else ""
        return "${code.code} - ${code.message}$swimmerSuffix"
    }

    override fun toString() = code.code
}