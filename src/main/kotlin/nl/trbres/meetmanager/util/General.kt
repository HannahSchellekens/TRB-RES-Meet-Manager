package nl.trbres.meetmanager.util

fun Any?.print() = print(this)
fun Any?.println() = println(this)
fun Any?.printf(format: String) = println(String.format(format, this))