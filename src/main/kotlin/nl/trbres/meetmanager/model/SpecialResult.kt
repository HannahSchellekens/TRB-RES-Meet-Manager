package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
data class SpecialResult(val type: SpecialResultType, val reason: String? = null) {

    override fun toString() = "$type${if (reason != null) ": $reason" else ""}"
}

/**
 * @author Ruben Schellekens
 */
enum class SpecialResultType(val abbreviation: String) {

    DISQUALIFIED("DIS"),
    DID_NOT_START("NG"),
    DID_NOT_START_WITHOUT_CANCELLATION("NGZA"),
    CANCELLED("AFGEM");

    override fun toString() = abbreviation
}