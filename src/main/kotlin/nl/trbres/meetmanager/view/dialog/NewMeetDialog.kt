package nl.trbres.meetmanager.view.dialog

import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import nl.trbres.meetmanager.model.AgeSet
import nl.trbres.meetmanager.model.CustomAgeGroup
import nl.trbres.meetmanager.model.Meet
import nl.trbres.meetmanager.time.toDate
import nl.trbres.meetmanager.util.FINE
import nl.trbres.meetmanager.util.OOPS
import nl.trbres.meetmanager.util.Oopsie
import nl.trbres.meetmanager.util.Reference
import nl.trbres.meetmanager.util.fx.styleClass
import tornadofx.*

/**
 * Dialog that prompts the user to create and select a new meet.
 *
 * @author Hannah Schellekens
 */
class NewMeetDialog : Fragment() {

    @Suppress("UNCHECKED_CAST")
    private val meet = params["meet"] as Reference<Meet>

    private lateinit var txtName: TextField
    private lateinit var txtLocation: TextField
    private lateinit var dateDate: DatePicker
    private lateinit var txtLanes: TextField
    private lateinit var cboxAgeSet: ComboBox<AgeSet>
    private lateinit var txtaCustomAges: TextArea

    override val root = borderpane {
        title = "Nieuwe zwemwedstrijd"
        prefWidth = 384.0

        center {
            form {
                fieldset("Wedstrijdinformatie") {
                    field("Wedstrijdnaam") {
                        txtName = textfield { useMaxWidth = true }
                    }
                    field("Locatie") {
                        txtLocation = textfield { useMaxWidth = true }
                    }
                    field("Datum") {
                        dateDate = datepicker()
                    }
                    field("Banen (n-m)") {
                        txtLanes = textfield { promptText = "vb. 2-5" }
                    }
                    field("Leeftijden") {
                        cboxAgeSet = combobox(values = AgeSet.values())
                    }
                    field("Extra leeftijden") {
                        txtaCustomAges = textarea {
                            promptText = """
                                Een categorie per regel in het volgende formaat:
                                "<Naam> <Y|O> <Samenvoeging1,Samenvoeging2,...>"

                                Gebruik 'Y' voor Jongens/Meisjes.
                                Gebruik 'O' voor Heren/Dames.
                                Gebruik '~' voor spaties.
                                Samenvoegingen betekent dat de categorie bij de aangegeven samenvoegingen
                                wordt gevoegd bij het indelen van zwemmers.
                            """.trimIndent().trim()
                        }
                    }
                }

                buttonbar {
                    button("Aanmaken").styleClass("green").action(::createMeet)
                    button("Annuleren").styleClass("red").action(::cancel)
                }
            }
        }
    }

    /**
     * Validates the input.
     *
     * @return `null` when there are problems, `Unit` when all is fine.
     */
    private fun validate(): Oopsie {
        if (txtName.text.isNullOrBlank()) {
            warning("Er is geen wedstrijdnaam opgegeven!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (txtLocation.text.isNullOrBlank()) {
            warning("Er is geen locatienaam opgegeven!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (dateDate.value == null) {
            warning("Er is geen datum geselecteerd!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (!txtLanes.text.matches(Regex("\\d+-\\d+"))) {
            warning("De gebruikte banen heeft een verkeerd formaat!", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (cboxAgeSet.selectedItem == null) {
            warning("Er is geen set van leeftijden opgegeven", owner = currentWindow, title = "Foute invoer")
            return OOPS
        }

        if (validateCustomAges().not()) {
            warning("Het formaat van de extra leeftijden is incorrect (<NAAM> <Y|O> per regel).")
            return OOPS
        }

        val numbers = txtLanes.text.split("-")
        val first = numbers[0].toInt()
        val second = numbers[1].toInt()
        if (first > second) {
            warning(
                    "Het tweede baannummer mag niet kleiner zijn dan het eerste nummer!",
                    owner = currentWindow,
                    title = "Foute invoer"
            )
            return OOPS
        }

        return FINE
    }

    /**
     * Validates the custom ages input.
     */
    private fun validateCustomAges(): Boolean {
        val text = txtaCustomAges.text
        if (text.isBlank()) return true

        return text.trim().split("\n").all {
            val split = it.split("""\s+""".toRegex())
            split.size > 1 && split[1].matches("""^[YOyo]$""".toRegex()) &&
                    (split.size > 2 && split.subList(2, split.size).joinToString(" ").matches("""([^,]+,)*([^,]+)""".toRegex()))
        }
    }

    /**
     * Parses the extra age groups to a list of `(Name, **Y**oung|**O**ld, JointList)` tuples.
     */
    private fun parseExtraAgeGroups(): List<Triple<String, String, Set<String>>> {
        val ages = txtaCustomAges.text
        if (ages.isBlank()) return emptyList()

        return ages.split("\n")
                .map {
                    val split = it.split("""\s+""".toRegex()).map { part -> part.replace("~", " ") }
                    val (name, youngOld) = split
                    val jointList = if (split.size <= 2) emptySet() else split.subList(2, split.size).joinToString(" ")
                            .split(",")
                            .toSet()
                    Triple(name, youngOld, jointList)
                }
    }

    /**
     * Creates the new meet and close the dialog.
     */
    private fun createMeet() {
        validate() ?: return

        val numbers = txtLanes.text.split("-")
        val laneRange = numbers[0].toInt()..numbers[1].toInt()
        var ageSet = cboxAgeSet.selectedItem!!

        val extraAges = parseExtraAgeGroups()
        if (extraAges.isNotEmpty()) {
            val groups = extraAges.map { (name, ageType, jointCategories) ->
                CustomAgeGroup(name, CustomAgeGroup.CategoryNameTranslator[ageType], jointCategories)
            }
            ageSet += AgeSet("", groups.toTypedArray())
        }

        meet.value = Meet(txtName.text, dateDate.value.toDate(), laneRange, txtLocation.text, ageSet = ageSet)

        currentStage?.close()
    }

    /**
     * Don't create a new meet and close the dialog.
     */
    private fun cancel() {
        currentStage?.close()
    }
}