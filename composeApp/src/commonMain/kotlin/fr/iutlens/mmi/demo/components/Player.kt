package fr.iutlens.mmi.demo.components

import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.game.sprite.BasicSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Player(
    res: DrawableResource,
    x: Float,
    y: Float,
    val mapArea: TiledArea,
    val joystickProvider: () -> JoystickPosition?
) : BasicSprite(res, x, y) {

    private var frameCounter = 0

    private fun possible(x: Float, y: Float): Boolean {
        with(mapArea) {
            val i = floor(x / w).toInt()
            val j = floor(y / h).toInt()
            if (i !in 0..<tileMap.geometry.sizeX ||
                j !in 0..<tileMap.geometry.sizeY)
                return false
            val code = tileMap.get(i,j)
            return code == 0
        }
    }

    override fun update() {
        super.update()
        val position = joystickProvider() ?: return

        val speed = position.x * mapArea.w / 4
        val maxSpeed = 45f
        val nextX = x + max(min(speed, maxSpeed), -maxSpeed)

        if (!position.isCentered && possible(nextX, y)) {
            x = nextX
            
            frameCounter++
            val animFrame = (frameCounter / 4) % 3

            if (speed > 0) {
                if (speed > maxSpeed/3) {
                    ndx = 10 + animFrame
                } else {
                    ndx = 0 + animFrame
                }
            } else if (speed < 0) {
                if (speed < -(maxSpeed/3)) {
                    ndx = 13 + animFrame
                } else {
                    ndx = 3 + animFrame
                }
            }
        } else {
            ndx = if (ndx >= 13) 13 else 10
        }
    }
}
