package nl.trbres.meetmanager.model

/**
 * @author Ruben Schellekens
 */
enum class SimpleAgeGroup(
        override val readableName: String,
        override val categoryName: (Category) -> String
) : AgeGroup {

    MINOREN("Minoren", { it.nameYoung }),
    JUNIOREN("Junioren", { it.nameYoung }),
    JEUGD("Jeugd", { it.nameYoung }),
    SENIOREN("Senioren", { it.nameOld });

    override val title = readableName
}

/**
 * @author Ruben Schellekens
 */
enum class DefaultAgeGroup(

        override val readableName: String,

        /**
         * The number of the age group.
         *
         * Or `0` for 'open'
         *
         * E.g. `6` for `Minioren 6`.
         */
        val groupNumber: Int,

        /**
         * On what simple age group the default age group is based.
         */
        val simple: SimpleAgeGroup

) : AgeGroup {

    MINIOREN_1("Minioren", 1, SimpleAgeGroup.MINOREN),
    MINIOREN_2("Minioren", 2, SimpleAgeGroup.MINOREN),
    MINIOREN_3("Minioren", 3, SimpleAgeGroup.MINOREN),
    MINIOREN_4("Minioren", 4, SimpleAgeGroup.MINOREN),
    MINIOREN_5("Minioren", 5, SimpleAgeGroup.MINOREN),
    MINIOREN_6("Minioren", 6, SimpleAgeGroup.MINOREN),
    JUNIOREN_1("Junioren", 1, SimpleAgeGroup.JUNIOREN),
    JUNIOREN_2("Junioren", 2, SimpleAgeGroup.JUNIOREN),
    JUNIOREN_3("Junioren", 3, SimpleAgeGroup.JUNIOREN),
    JUNIOREN_4("Junioren", 4, SimpleAgeGroup.JUNIOREN),
    JEUGD_1("Jeugd", 1, SimpleAgeGroup.JEUGD),
    JEUGD_2("Jeugd", 2, SimpleAgeGroup.JEUGD),
    SENIOREN_1("Senioren", 2, SimpleAgeGroup.SENIOREN),
    SENIOREN_2("Senioren", 2, SimpleAgeGroup.SENIOREN),
    SENOREN_OPEN("Senioren", 0, SimpleAgeGroup.SENIOREN);

    companion object {

        @JvmField
        val MALE_GROUPS = listOf(
                MINIOREN_1, MINIOREN_2, MINIOREN_3, MINIOREN_4, MINIOREN_5, MINIOREN_6,
                JUNIOREN_1, JUNIOREN_2, JUNIOREN_3, JUNIOREN_4,
                JEUGD_1, JEUGD_2,
                SENOREN_OPEN
        )

        @JvmField
        val FEMALE_GROUPS = listOf(
                MINIOREN_1, MINIOREN_2, MINIOREN_3, MINIOREN_4, MINIOREN_5,
                JUNIOREN_1, JUNIOREN_2, JUNIOREN_3,
                JEUGD_1, JEUGD_2,
                SENIOREN_1, SENIOREN_2, SENOREN_OPEN
        )
    }

    override val title = "$readableName ${if (groupNumber == 0) "open" else groupNumber.toString()}"
    override val categoryName = simple.categoryName
}

/**
 * @author Ruben Schellekens
 */
interface AgeGroup {

    /**
     * The human readable name of the age group.
     */
    val readableName: String

    /**
     * Given a category, generatesa human readable name of the category..
     */
    val categoryName: (Category) -> String

    /**
     * The total name of the category.
     *
     * E.g. `Minioren 2`, `Senioren`.
     */
    val title: String

    /**
     * See [categoryName].
     */
    operator fun get(category: Category) = categoryName(category)
}

