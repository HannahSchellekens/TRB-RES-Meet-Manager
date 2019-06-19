package nl.trbres.meetmanager.model

/**
 * @author Hannah Schellekens
 */
enum class Category(val nameYoung: String, val nameOld: String) {

    MALE("Jongens", "Heren"),
    FEMALE("Meisjes", "Dames"),
    MIX("Gemengd", "Gemengd");

    fun isInCategory(category: Category) = this == category || this == MIX

    override fun toString() = nameOld
}