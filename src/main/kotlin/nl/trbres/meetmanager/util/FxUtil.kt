package nl.trbres.meetmanager.util

import javafx.scene.Node
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.UIComponent

/**
 * Adds the icon as graphic to the node.
 */
fun MenuItem.icon(icon: Image): MenuItem {
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