package nl.trbres.meetmanager.util

import nl.trbres.meetmanager.MeetManagerApp
import java.awt.Desktop
import java.io.File

/**
 * The current program version number.
 */
val APPLICATION_VERSION = MeetManagerApp::class.java.`package`.implementationVersion ?: "indev"

/**
 * The title of the program (duh),
 */
val APPLICATION_NAME = MeetManagerApp::class.java.`package`.implementationTitle ?: "TRB-RES Meet Manager"

/**
 * The github URL of the program.
 */
val GITHUB_PAGE = "https://github.com/RubenSchellekens/TRB-RES-Meet-Manager"

/**
 * The URL where the documentation of the software is located.
 */
val DOCUMENTATION_PAGE = "$GITHUB_PAGE/wiki"

/**
 * The URL pointing to the program's license.
 */
val LICENSE = "$GITHUB_PAGE/blob/master/LICENSE"

/**
 * The URL of the homepage of the software's author.
 */
val AUTHOR_HOME = "https://rubenschellekens.github.io"

/**
 * Folder where all the application data will be stored.
 */
val APP_DATA_HOME = System.getProperty("user.home") + "/.trbmeet"

/**
 * Creates the [APP_DATA_HOME] folder if it doesn't yet exist.
 */
fun createAppData() {
    val dir = File(APP_DATA_HOME)
    if (!dir.exists()) {
        dir.mkdir()
    }
}

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

/**
 * Returns `true` if the object is `null`.
 */
fun Any?.isNull() = this == null

/**
 * Returns `true` if the object is not `null`.
 */
fun Any?.isNotNull() = this != null

fun String.file() = File(this)
fun File.open() = Desktop.getDesktop().open(this)
fun Any?.print() = print(this)
fun Any?.println() = println(this)
fun Any?.printf(format: String) = println(String.format(format, this))