package fr.iutlens.mmi.demo.components

import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.game.sprite.PhysicsSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource

class Player(
    res: DrawableResource,
    x: Float,
    y: Float,
    mapArea: TiledArea,
    val joystickProvider: () -> JoystickPosition?
) : PhysicsSprite(res, x, y, mapArea, gravity = 2f, jumpForce = -55f) {

    private var frameCounter = 0

    private val jumpRisingFrame = 21
    private val jumpFallingFrame = 23

    override fun update() {
        val position = joystickProvider() ?: return
        
        if (position.y < -0.6f) {
            jump()
        }

        val speed = position.x * mapArea.w / 4
        val maxSpeed = 30f
        moveX(speed)

        applyPhysics()
        
        if (!isOnGround) {
            ndx = if (vy < 0) jumpRisingFrame else jumpFallingFrame
        } else if (!position.isCentered && speed != 0f) {
            frameCounter++
            val animFrame = (frameCounter / 4) % 3
            
            val isRunning = (speed > maxSpeed || speed < -maxSpeed)

            if (speed > 0) {
                ndx = if (isRunning) 10 + animFrame else 0 + animFrame
            } else {
                ndx = if (isRunning) 13 + animFrame else 3 + animFrame
            }
        } else {
            ndx = if (ndx in 13..15 || ndx == 3 || speed < 0) 13 else 10
        }
        
        super.update()
    }
}
