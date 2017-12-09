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

fun Any?.print() = print(this)
fun Any?.println() = println(this)
fun Any?.printf(format: String) = println(String.format(format, this))