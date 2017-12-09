package nl.trbres.meetmanager.util

import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView

/**
 * Adds the icon as graphic to the node.
 */
fun MenuItem.icon(icon: Image): MenuItem {
    graphic = ImageView(icon)
    return this
}