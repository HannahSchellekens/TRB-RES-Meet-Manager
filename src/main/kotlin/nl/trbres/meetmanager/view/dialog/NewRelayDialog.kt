package nl.trbres.meetmanager.view.dialog

import javafx.scene.control.ButtonType
import javafx.scene.control.ListView
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.Icons
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.Category
import nl.trbres.meetmanager.model.Club
import nl.trbres.meetmanager.model.Relay
import nl.trbres.meetmanager.model.Swimmer
import nl.trbres.meetmanager.util.fx.icon
import tornadofx.*

/**
 * @author Ruben Schellekens
 */
open class NewRelayDialog(currentWindow: Window? = null) : NewSwimmerDialog(currentWindow) {

    protected lateinit var listSwimmers: ListView<Swimmer>

    override val swimmerText: String
        get() = "estaffette"

    init {
        // Modify original form.
        cboxCategory.items = (cboxCategory.items + Category.MIX).observable()
        cboxClub.selectionModel.selectedItemProperty().addListener { _ ->
            val club = cboxClub.selectedItem ?: return@addListener
            txtName.text = "${club.name} ${club.nextClubNumber() ?: 1}"
        }

        // Add new elements
        fieldset.apply {
            field("Zwemmers") {
                listSwimmers = listview {
                    prefHeight = 175.0

                    contextmenu {
                        item("Toevoegen").icon(Icons.add).action(::addSwimmer)
                        item("Verwijderen").icon(Icons.remove).action(::removeSwimmer)
                    }
                }
            }
        }

        // Relay instead of swimmer.
        resultConverter = Callback {
            if (it != ButtonType.OK) {
                return@Callback null
            }

            Relay(
                    txtName.text.trim(),
                    cboxAgeGroup.selectedItem!!,
                    cboxCategory.selectedItem!!,
                    cboxClub.selectedItem?.mapToClub() ?: Club.NO_CLUB
            ).apply {
                members.addAll(listSwimmers.items)
            }
        }
    }

    /**
     * Fills all the fields with the info of the given Relay object.
     */
    fun fillInfo(relay: Relay) {
        cboxCategory.selectionModel.select(relay.category)
        cboxClub.selectionModel.select(relay.club)
        cboxAgeGroup.selectionModel.select(relay.age)
        listSwimmers.items = relay.members.observable()
        txtName.text = relay.name
    }

    /**
     * Prompt the user to add a swimmer to the list.
     */
    private fun addSwimmer() {
        ChooseSwimmerDialog(ownerWindow, relay = false).showAndWait().ifPresent {
            it.swimmer.ifPresent {
                listSwimmers.items.add(it)
            }
        }
    }

    /**
     * Removes the selected swimmer from the list view.
     */
    private fun removeSwimmer() {
        val selected = listSwimmers.selectedItem ?: return
        listSwimmers.items.remove(selected)
    }

    /**
     * Looks through all the swimmers looking for the next club's number.
     *
     * So if there is a CLUB 1, then this method will return 2.
     * If there is a CLUB 12, then there will be a return value of 13.
     * If there is no CLUB with a number, then it will return null.
     */
    private fun Club.nextClubNumber(): Int? {
        val meet = State.meet ?: return null
        val endsWithNumber = Regex(".+ \\d+$")
        val max = meet.swimmers.asSequence()
                .filter { it.name.startsWith(this.name) && it.name.matches(endsWithNumber) }
                .mapNotNull { it.name.split(" ").last().toIntOrNull() }
                .max()

        return if (max == null) {
            return null
        }
        else max + 1
    }
}