package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * @author Ruben SChellekens
 */
enum class AgeSet(

        /**
         * The readable name of the age set.
         */
        val readableName: String,

        /**
         * All the available age groups in this age set.
         */
        val ages: Array<out AgeGroup>
) {

    /**
     * [SimpleAgeGroup].
     */
    SIMPLE("Minioren/Junioren/Jeugd/Senioren", SimpleAgeGroup.values()),

    /**
     * [PrimarySchoolGroup].
     */
    PRIMARY_SCHOOL("Basisschool", PrimarySchoolGroup.values()),

    /**
     * [DefaultAgeGroup].
     */
    COMPETITIVE("Wedstrijdzwemmen", DefaultAgeGroup.values());

    override fun toString() = readableName
}

/**
 * @author Ruben Schellekens
 */
@JsonTypeName("simple")
enum class SimpleAgeGroup(
        override val readableName: String,
        override val categoryName: (Category) -> String
) : AgeGroup {

    MINOREN("Minioren", { it.nameYoung }),
    JUNIOREN("Junioren", { it.nameYoung }),
    JEUGD("Jeugd", { it.nameYoung }),
    SENIOREN("Senioren", { it.nameOld });

    override val title = readableName
    override val id = name

    override fun isJointCategory(other: AgeGroup) = when (other) {
        is DefaultAgeGroup -> other.simple == this
        else -> false
    }

    override fun toString() = title
}

/**
 * @author Ruben Schellekens
 */
@JsonTypeName("school")
enum class PrimarySchoolGroup(
        override val readableName: String,
        override val categoryName: (Category) -> String
) : AgeGroup {

    GROEP_4("Groep 4", { it.nameYoung }),
    GROEP_5("Groep 5", { it.nameYoung }),
    GROEP_6("Groep 6", { it.nameYoung }),
    GROEP_7("Groep 7", { it.nameYoung }),
    GROEP_8("Groep 8", { it.nameYoung }),
    GROEP_4_WZ("Groep 4 Wz", { it.nameYoung }),
    GROEP_5_WZ("Groep 5 Wz", { it.nameYoung }),
    GROEP_6_WZ("Groep 6 Wz", { it.nameYoung }),
    GROEP_7_WZ("Groep 7 Wz", { it.nameYoung }),
    GROEP_8_WZ("Groep 8 Wz", { it.nameYoung });

    override val title = readableName
    override val id = name

    override fun isJointCategory(other: AgeGroup) = other == this || when (this) {
        GROEP_4 -> other == GROEP_4_WZ
        GROEP_5 -> other == GROEP_5_WZ
        GROEP_6 -> other == GROEP_6_WZ
        GROEP_7 -> other == GROEP_7_WZ
        GROEP_8 -> other == GROEP_8_WZ
        GROEP_4_WZ -> other == GROEP_4
        GROEP_5_WZ -> other == GROEP_5
        GROEP_6_WZ -> other == GROEP_6
        GROEP_7_WZ -> other == GROEP_7
        GROEP_8_WZ -> other == GROEP_8
        else -> false
    }

    override fun toString() = title
}

/**
 * @author Ruben Schellekens
 */
@JsonTypeName("default")
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
    override val id = name

    override fun isJointCategory(other: AgeGroup) = simple == other

    override fun toString() = title
}

/**
 * @author Ruben Schellekens
 */
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT, property = "implementation")
@JsonSubTypes(
        JsonSubTypes.Type(value = DefaultAgeGroup::class, name = "default"),
        JsonSubTypes.Type(value = SimpleAgeGroup::class, name = "simple"),
        JsonSubTypes.Type(value = PrimarySchoolGroup::class, name = "school")
)
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
     * The unique id of the group.
     */
    val id: String

    /**
     * See [categoryName].
     */
    operator fun get(category: Category) = categoryName(category)

    /**
     * `true` if both age groups are seen the same, `false` otherwise.
     */
    fun isJointCategory(other: AgeGroup): Boolean
}

