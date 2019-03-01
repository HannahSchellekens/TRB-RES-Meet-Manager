package nl.trbres.meetmanager.view

import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.time.toDate
import nl.trbres.meetmanager.util.fx.onUnfocus
import nl.trbres.meetmanager.util.fx.validate
import nl.trbres.meetmanager.util.toIntRange
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class General(val main: MainView) : BorderPane() {

    lateinit var txtName: TextField
    lateinit var txtLocation: TextField
    lateinit var dateDate: DatePicker
    lateinit var txtLanes: TextField
    lateinit var txtPenalty: TextField
    lateinit var txtOrganisation: TextField

    init {
        center {
            form {
                fieldset("Wedstrijdinformatie") {
                    field("Wedstrijdnaam") {
                        txtName = textfield().validate(
                                { !text.isNullOrBlank() },
                                "Text mag niet leeg zijn",
                                { State.meet?.name = text; main.updateTitle() },
                                { text = State.meet?.name ?: "" }
                        )
                    }
                    field("Locatie") {
                        txtLocation = textfield().validate(
                                { !text.isNullOrBlank() },
                                "Locatienaam mag niet leeg zijn",
                                { State.meet?.location = text },
                                { text = State.meet?.location ?: "" }
                        )
                    }
                    field("Datum") {
                        dateDate = datepicker().onUnfocus {
                            State.meet?.date = value.toDate()
                        }
                    }
                    field("Banen in gebruik (\"x-y\")") {
                        val validation = Regex("\\d+-\\d+")
                        txtLanes = textfield().validate(
                                {
                                    if (!text.matches(validation)) {
                                        return@validate false
                                    }
                                    val numbers = text.split("-")
                                    return@validate numbers[0].toInt() <= numbers[1].toInt()
                                },
                                "De gebruikte banen hebben een verkeerd formaat.\nVerwacht: 'n-m' waar n & m baannummers zijn (n <= m).\nVoorbeeld: 1-8 voor banen 1 t/m 8.",
                                { State.meet?.lanes = text.toIntRange() },
                                { val range = State.meet?.lanes ?: return@validate; text = "${range.start}-${range.endInclusive}" }
                        )
                    }
                    field("Straf voor diskwalificatie (seconden)") {
                        val validation = Regex("\\d*[.]?\\d\\d?")
                        txtPenalty = textfield().validate(
                                {
                                        return@validate text.matches(validation)
                                },
                                "Invoer is een ongeldig aantal seconden (hondersten scheiden met een punt).",
                                { State.meet?.penalty = (text.toFloat() * 100).toInt() },
                                { text = State.meet?.penalty?.toString() ?: "" }
                        )
                    }
                    field("Naam organisatie") {
                        txtOrganisation = textfield().validate(
                                { true },
                                "",
                                { State.meet?.organiser = text },
                                { text = State.meet?.organiser ?: "TRB-RES" }
                        )
                    }
                }
            }
        }

        populate()
    }

    fun populate() {
        val meet = State.meet ?: return
        txtName.text = meet.name
        txtLocation.text = meet.location
        dateDate.value = meet.date.toLocalDate()
        txtLanes.text = "${meet.lanes.first}-${meet.lanes.endInclusive}"
        txtPenalty.text = "%.2f".format(meet.penalty.toFloat() / 100f)
        txtOrganisation.text = meet.organiser
    }
}