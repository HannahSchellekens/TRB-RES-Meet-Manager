package nl.trbres.meetmanager

import javafx.application.Application
import nl.trbres.meetmanager.util.deserialize
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
    State.meet = json.deserialize()
    Application.launch(MeetManagerApp::class.java, *args)
}

val json = """{"name":"Swimkick Deel 1 Met Ruubje","location":"Tilburg","date":{"year":2017,"month":12,"day":8},"lanes":{"start":2,"end":5},"events":[{"distance":{"metres":50,"times":1,"title":"50m"},"stroke":"BACKSTROKE","category":"MALE","ages":[{"default":"SENIOREN"}],"heats":[{"lanes":{"3":{"name":"Ruubje Schellekens","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"926dc895-42c3-4ab1-b7f1-623395c7e5ec"},"4":{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"}},"results":{"3":{"hours":0,"minutes":0,"seconds":29,"hundreths":57},"4":{"hours":0,"minutes":0,"seconds":31,"hundreths":0}},"statusses":{}}]},{"distance":{"metres":50,"times":1,"title":"50m"},"stroke":"BACKSTROKE","category":"FEMALE","ages":[{"default":"JUNIOREN"}],"heats":[{"lanes":{"2":{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"},"3":{"name":"Frana Renssen","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"3e990c6f-b7e9-4a90-9729-22604ad3d641"},"4":{"name":"Kleintje Vissers","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"efc0b018-8638-4079-973b-b36b4fb442ff"}},"results":{"3":{"hours":0,"minutes":0,"seconds":32,"hundreths":84}},"statusses":{"2":{"type":"DID_NOT_START","reason":null},"4":{"type":"DISQUALIFIED","reason":"Te vroeg bewogen voor de start."}}}]}],"clubs":[{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"}],"swimmers":[{"name":"Ruubje Schellekens","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"926dc895-42c3-4ab1-b7f1-623395c7e5ec"},{"name":"Frana Renssen","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"3e990c6f-b7e9-4a90-9729-22604ad3d641"},{"name":"Kleintje Vissers","age":{"default":"JUNIOREN"},"category":"FEMALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"efc0b018-8638-4079-973b-b36b4fb442ff"},{"name":"Lars Hurks","age":{"default":"SENIOREN"},"category":"MALE","club":{"name":"TRB-RES","id":"bbf01a36-f5ca-439a-9ef6-c11f8bec83a4"},"id":"39b8cf3a-7f00-480a-8faa-823a9cd4f6d0"}]}"""