package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
enum class Category(val nameYoung: String, val nameOld: String) {

    MALE("Jongens", "Heren"),
    FEMALE("Meisjes", "Dames"),
    MIX("Gemengd", "Gemengd")
}