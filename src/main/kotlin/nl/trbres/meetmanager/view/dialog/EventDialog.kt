package nl.trbres.meetmanager.view.dialog

import javafx.scene.Node
import javafx.scene.control.*
import javafx.stage.Window
import javafx.util.Callback
import nl.trbres.meetmanager.State
import nl.trbres.meetmanager.model.*
import nl.trbres.meetmanager.util.isNaturalNumber
import nl.trbres.meetmanager.util.isNull
import tornadofx.*

/**
 * @author Hannah Schellekens
 */
open class EventDialog(ownerWindow: Window?, var editEvent: Event? = null) : Dialog<Event>() {

    private lateinit var txtTimes: TextField
    private lateinit var txtDistance: TextField
    private lateinit var cboxStroke: ComboBox<Stroke>
    private lateinit var cboxCategory: ComboBox<Category>
    private lateinit var cboxAgeGroup: ComboBox<AgeGroup>
    private lateinit var cboxMetric: ComboBox<Event.Metric>
    private lateinit var checkModifiers: MutableMap<Event.Modifier, CheckBox>

    init {
        title = if (editEvent == null) "Nieuw programma" else "Programma bewerken"
        headerText = title
        dialogPane.minWidth = 350.0
        initOwner(ownerWindow)

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.isDisable = true

        resultConverter = Callback {
            if (it != ButtonType.OK) {
                return@Callback null
            }

            return@Callback if (editEvent == null) Event(
                    Distance(txtDistance.text.toInt(), txtTimes.text.toInt()),
                    cboxStroke.selectedItem!!,
                    cboxCategory.selectedItem!!,
                    mutableListOf(cboxAgeGroup.selectedItem!!),
                    cboxMetric.selectedItem ?: kotlin.error("No selected item found"),
                    extractModifiers()
            )
            else editEvent!!.apply {
                distance = Distance(txtDistance.text.toInt(), txtTimes.text.toInt())
                stroke = cboxStroke.selectedItem!!
                category = cboxCategory.selectedItem!!
                ages = mutableListOf(cboxAgeGroup.selectedItem!!)
                metric = cboxMetric.selectedItem ?: Event.Metric.TIME
                modifiers = extractModifiers()
            }
        }

        dialogPane.content = form {
            fieldset {
                field("Afstand") {
                    hbox {
                        txtTimes = textfield("1").validation(okButton)
                        txtTimes.prefWidth = 32.0
                        label(" × ") {
                            prefHeight = 25.0
                        }
                        txtDistance = textfield().validation(okButton)
                        txtDistance.prefWidth = 64.0
                        label("m") {
                            prefHeight = 25.0
                        }
                    }
                }
                field("Zwemslag") {
                    cboxStroke = combobox {
                        items = Stroke.values().toList().observable()
                        validation(okButton)
                    }
                }
                field("Leeftijdscategorie") {
                    cboxAgeGroup = combobox {
                        items = State.meet!!.ageSet.ages.toList().observable()
                        validation(okButton)
                    }
                }
                field("Categorie") {
                    cboxCategory = combobox {
                        items = Category.values().toList().observable()
                        validation(okButton)
                    }
                }
                field("Metriek") {
                    cboxMetric = combobox {
                        items = Event.Metric.values().toList().observable()
                        selectionModel.select(Event.Metric.TIME)
                        validation(okButton)
                    }
                }
                field("Opties") {
                    checkModifiers = HashMap()
                    Event.Modifier.values().forEach { modifier ->
                        checkModifiers[modifier] = checkbox(modifier.title)
                    }
                }
            }
        }

        fillDefaults()
        runLater {
            txtDistance.requestFocus()
        }
    }

    /**
     * Fills in the values of a given event [editEvent] if it exists.
     */
    private fun fillDefaults() {
        if (editEvent == null) {
            return
        }

        txtTimes.text = editEvent!!.distance.times.toString()
        txtDistance.text = editEvent!!.distance.metres.toString()
        cboxStroke.selectionModel.select(editEvent!!.stroke)
        cboxCategory.selectionModel.select(editEvent!!.category)
        cboxAgeGroup.selectionModel.select(editEvent!!.ages.first())
        cboxMetric.selectionModel.select(editEvent!!.metric)

        Event.Modifier.values().forEach { modifier ->
            checkModifiers[modifier]?.isSelected = modifier in editEvent!!.modifiers
        }
    }

    /**
     * Validates the input, and if it is correct, enables the okButton. Otherwise it disables it.
     */
    private fun validate(okButton: Node) {
        okButton.isDisable = !validateDistance() ||
                cboxStroke.selectedItem.isNull() ||
                cboxAgeGroup.selectedItem.isNull() ||
                cboxCategory.selectedItem.isNull()
    }

    private fun validateDistance(): Boolean {
        val timesText = txtTimes.text
        val distanceText = txtDistance.text

        if (timesText.isNullOrBlank() || distanceText.isNullOrBlank()) {
            return false
        }
        if (!timesText.isNaturalNumber() || !distanceText.isNaturalNumber()) {
            return false
        }

        val times = timesText.toInt()
        val distance = distanceText.toInt()
        return !(times == 0 || distance == 0 || distance % 25 != 0)
    }

    private fun TextField.validation(okButton: Node): TextField {
        textProperty().addListener { _ -> validate(okButton) }
        return this
    }

    private fun <C> ComboBox<C>.validation(okButton: Node): ComboBox<C> {
        selectionModel.selectedItemProperty().addListener { _ -> validate(okButton) }
        return this
    }

    private fun extractModifiers(): Set<Event.Modifier> = checkModifiers
            .filter { (_, box) -> box.isSelected }
            .map { (modifier, _) -> modifier }.toSet()
}