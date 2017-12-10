package nl.trbres.meetmanager.util.fx

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.util.Callback
import tornadofx.UIComponent
import tornadofx.warning

/**
 * Adds the icon as graphic to the node.
 */
fun MenuItem.icon(icon: Image): MenuItem {
    graphic = ImageView(icon)
    return this
}

/**
 * Adds the icon as graphic to the node.
 */
fun Button.icon(icon: Image): Button{
    graphic = ImageView(icon)
    return this
}

/**
 * Adds the given style class to the node.
 */
fun <T : Node> T.styleClass(styleClass: String): T {
    this.styleClass += styleClass
    return this
}

/**
 * Adds all given style classes to the node.
 */
fun <T : Node> T.styleClass(styleClass: String, vararg style: String): T {
    styleClass(styleClass).styleClass.addAll(style)
    return this
}

/**
 * Set the width and height of a ui element.
 */
fun UIComponent.dimensions(width: Number, height: Number) {
    currentStage?.width = width.toDouble()
    currentStage?.height = height.toDouble()
}

/**
 * Validates the contents of the node and applies it when correct and discards it when incorrect.
 *
 * @param validation
 *          Checks if the input is correct: `true` when correct, `false` when incorrect.
 * @param message
 *          The message to show when the value is incorrect.
 * @param store
 *          How to store the input when it's correct.
 * @param fetch
 *          How to restore the input when it's incorrect.
 */
inline fun <N : Node> N.validate(crossinline validation: N.() -> Boolean, message: String,
                                 crossinline store: N.() -> Unit, crossinline fetch: N.() -> Unit): N {
    enterToUnfocus().onUnfocus {
        if (validation()) {
            store()
            return@onUnfocus
        }

        warning(message, title = "Foute invoer", owner = scene.window)
        fetch()
    }
    return this
}

/**
 * Executes the given action whenever the element gets unfocused.
 */
inline fun <N : Node> N.onUnfocus(crossinline action: N.() -> Unit): N {
    focusedProperty().addListener { _ ->
        if (!isFocused) {
            action()
        }
    }
    return this
}

/**
 * Removes the focus from the node whenever the enter key is pressed.
 */
fun <N : Node> N.enterToUnfocus(): N {
    setOnKeyReleased { e ->
        if (e.code == KeyCode.ENTER) {
            parent.requestFocus()
        }
    }
    return this
}

/**
 * Makes a certain table column editable with a combobox.
 *
 * @param setter
 *          How the row must be modified given the new value.
 * @param options
 *          All the options that must appear in the combo box.
 */
fun <S, T : Any?> TableColumn<S, T>.makeEditable(setter: (S, T) -> Unit, options: () -> List<T>) = apply {
    tableView?.isEditable = true
    isEditable = true
    cellFactory = Callback { _ -> ComboBoxEditingCell<S, T>(setter, options) }
}

/**
 * Executes the given code when a tab gets opened.
 */
inline fun Tab.openEvent(crossinline block: Tab.() -> Unit): Tab {
    onSelectionChanged = EventHandler {
        if (isSelected) {
            block()
        }
    }
    return this
}