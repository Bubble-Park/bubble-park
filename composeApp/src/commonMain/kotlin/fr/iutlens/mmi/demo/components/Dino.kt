package fr.iutlens.mmi.demo.components

import androidx.compose.ui.geometry.Rect
import fr.iutlens.mmi.demo.game.sprite.PhysicsSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource

abstract class Dino(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    gravity: Float = 8f,
    jumpForce: Float = -110f
) : PhysicsSprite(res, x, y, mapArea, gravity, jumpForce) {

    var stunTimer = 0
    var jumpCooldown = 0
    val radius = 60f
    var isDead = false

    override val boundingBox: Rect
        get() = Rect(x - radius, y - radius, x + radius, y + radius)
}
