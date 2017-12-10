package nl.trbres.meetmanager.util.fx

import javafx.scene.control.ComboBox
import javafx.scene.control.TableCell
import tornadofx.observable
import tornadofx.rowItem

/**
 * @author Ruben Schellekens
 */
open class ComboBoxEditingCell<S, T>(

        /**
         * Sets the value of `T` in `S`.
         */
        private val setNewValue: (S, T) -> Unit,

        /**
         * Generates a list of all the options that must be available in the combo box.
         */
        private val options: () -> List<T>

) : TableCell<S, T>() {

    private var comboBox: ComboBox<T> = createComboBox()

    override fun startEdit() {
        if (isEmpty) {
            return
        }

        super.startEdit()
        text = null
        graphic = createComboBox()
        comboBox.selectionModel.select(item)
    }

    override fun cancelEdit() {
        super.cancelEdit()

        text = item?.toString()
        graphic = null
    }

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            text = text()
            graphic = null
            return
        }

        text = text()
        if (isEditing) {
            comboBox.value = item
            graphic = comboBox
            return
        }

        graphic = null
    }

    private fun text() = item?.toString() ?: ""

    private fun createComboBox(): ComboBox<T> {
        comboBox = ComboBox(options().observable())
        comboBox.selectionModel.selectedItemProperty().addListener { _ ->
            item = comboBox.selectionModel.selectedItem
            setNewValue(rowItem, item)
        }
        return comboBox
    }
}