package nl.trbres.meetmanager

import javafx.application.Application
import nl.trbres.meetmanager.view.MainView
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.setStageIcon

/**
 * @author Ruben Schellekens
 */
class MeetManagerApp : App(MainView::class) {

    init {
        setStageIcon(Icons.trbResLogo)
        importStylesheet("/style/main.css")
    }
}

fun main(args: Array<String>) {
    Application.launch(MeetManagerApp::class.java, *args)
}