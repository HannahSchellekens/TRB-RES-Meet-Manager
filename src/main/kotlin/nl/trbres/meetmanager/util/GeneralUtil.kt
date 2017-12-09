package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.MeetManagerApp

/**
 * The current program version number.
 */
val APPLICATION_VERSION = MeetManagerApp::class.java.`package`.implementationVersion ?: "indev"

/**
 * The title of the program (duh),
 */
val APPLICATION_NAME = MeetManagerApp::class.java.`package`.implementationTitle ?: "TRB-RES Meet Manager"

/**
 * An `Oopsie` type is either:
 * `null` when problems arise (or [OOPS]), or
 * `Unit` when all went fine (or [FINE]).
 *
 * Oopsie values can be used in conjunction with the elvis operator: they are like super booleans! Example:
 *
 * ```
 * fun dirtyFun(): Oopsie = OOPS
 * fun safeFun() {
 *     dirtyFun() ?: panic()
 * }
 * ```
 */
typealias Oopsie = Unit?

/**
 * All went fine.
 */
val FINE = Unit

/**
 * Something went wrong.
 */
val OOPS: Unit? = null

/**
 * Rethrows all errors with the given message.
 *
 * @return The value returned by `codeBlock`.
 */
inline fun <T> usefulTry(message: String, codeBlock: () -> T): T {
    try {
        return codeBlock()
    }
    catch (e: Exception) {
        throw Exception(message, e)
    }
}

/**
 * Executes code when the given object is null.
 */
inline fun <T> T?.whenNull(block: () -> Unit) = this ?: block()

/**
 * Executes code when the given object is not null.
 */
inline fun <T> T?.whenNonNull(block: (T) -> Unit) {
    if (this != null) {
        block(this)
    }
}

fun Any?.print() = print(this)
fun Any?.println() = println(this)
fun Any?.printf(format: String) = println(String.format(format, this))