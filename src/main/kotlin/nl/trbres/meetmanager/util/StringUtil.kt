package nl.trbres.meetmanager.util

/**
 * Parses a string of format `n-m` to an int range `n..m`.
 */
fun String.toIntRange(): IntRange {
    val split = split("-")
    return split[0].toInt()..split[1].toInt()
}