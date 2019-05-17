package nl.trbres.meetmanager.model

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.trbres.meetmanager.time.Time

/**
 * @author Hannah Schellekens
 */
data class Event(

        /**
         * The distance that will be swum.
         */
        var distance: Distance,

        /**
         * The stroke that will be swum.
         */
        var stroke: Stroke,

        /**
         * Who participate in the event.
         */
        var category: Category,

        /**
         * What age groups are allowed in the event.
         */
        var ages: MutableList<AgeGroup>,

        /**
         * With what kind of metric the results are measured.
         */
        var metric: Metric = Metric.TIME,

        /**
         * Special features of an event.
         */
        var modifiers: Set<Modifier> = emptySet()
) {

    /**
     * All heats of the event.
     */
    val heats: MutableList<Heat> = ArrayList()

    /**
     * `true` if the event is a relay, `false` if it is an individual event.
     */
    @JsonIgnore
    fun isRelay() = distance.times > 1

    /**
     * Checks if all heats in the event have their results filled in.
     */
    @JsonIgnore
    fun isFinished() = heats.all { it.isFinished() }

    /**
     * Collects all the swim results from the event, i.e. the swimmers with their swum times/statuses.
     *
     * The list has the result in order.
     */
    fun swimResults(convertTo: Int? = null): List<SwimResult> {
        val results = heats.flatMap {
            val factor = if (convertTo == null) 1f else convertTo.toFloat() / distance.metres.toFloat()
            it.swimResults(factor)
        }

        val sortingOrder = if (metric == Metric.TIME) 1 else -1
        val regular = results.asSequence()
                .filter { it.status == null }
                .sortedBy { it.result.toHundreths() * sortingOrder }
                .toList()
        val disqualified = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DISQUALIFIED }
                .sortedBy { it.result.toHundreths() * sortingOrder }
                .toList()
        val didNotStart = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DID_NOT_START }
                .sortedBy { it.swimmer.name }
                .toList()
        val didNotStartWithoutCancellation = results.asSequence()
                .filter { it.status?.type == SpecialResultType.DID_NOT_START_WITHOUT_CANCELLATION }
                .sortedBy { it.swimmer.name }
                .toList()

        return regular + disqualified + didNotStart + didNotStartWithoutCancellation
    }

    override fun toString(): String {
        val options = modifiers.sorted().joinToString(" ").trim()
        return if (metric == Metric.TIME) {
            "${ages.first()[category]} ${ages.joinToString(",")}, $distance $stroke $options".trimEnd()
        }
        else "${ages.first()[category]} ${ages.joinToString(",")}, $stroke $options".trimEnd()
    }

    fun toStringNoAges(): String {
        val options = modifiers.sorted().joinToString(" ").trim()
        return if (metric == Metric.TIME) {
            "${ages.first()[category]}, $distance $stroke $options".trimEnd()
        }
        else "${ages.first()[category]}, $stroke $options".trimEnd()
    }

    /**
     * @author Hannah Schellekens
     */
    enum class Modifier(val title: String) {

        ARMS("armen"),
        LEGS("benen"),
        UNDERWATER("onderwater");

        override fun toString() = title
    }

    /**
     * @author Hannah Schellekens
     */
    enum class Metric(val description: String, val endResult: String) {

        TIME("tijd", "eindtijd"),
        DISTANCE("afstand", "afstand");

        override fun toString() = description
    }
}

/**
 * @author Hannah Schellekens
 */
data class SwimResult(
        val swimmer: Swimmer,
        val result: Time,
        val status: SpecialResult?,
        val disqualification: Disqualification?
) : Comparable<SwimResult> {

    override fun compareTo(other: SwimResult): Int {
        return result.compareTo(other.result)
    }
}