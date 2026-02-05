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
) : PhysicsSprite(res, x, y, mapArea, gravity = 8f, jumpForce = -110f) {

    private var frameCounter = 0
    private var facingRight = true

    private val jumpRisingFrame = 21
    private val jumpFallingFrame = 23
    private val jumpRisingFrameLeft = 26
    private val jumpFallingFrameLeft = 29

    override fun update() {
        val position = joystickProvider() ?: return
        
        if (position.y < -0.6f) {
            jump()
        }

        val speed = position.x * mapArea.w / 4
        if (speed > 0) facingRight = true
        if (speed < 0) facingRight = false

        moveX(speed, 60f)

        applyPhysics()
        
        if (!isOnGround) {
            if (facingRight) {
                ndx = if (vy < 0) jumpRisingFrame else jumpFallingFrame
            } else {
                ndx = if (vy < 0) jumpRisingFrameLeft else jumpFallingFrameLeft
            }
        } else if (!position.isCentered && speed != 0f) {
            frameCounter++
            val animFrame = (frameCounter / 4) % 3
            
            val isRunning = (speed > 20f || speed < -20f)

            if (facingRight) {
                ndx = if (isRunning) 10 + animFrame else 0 + animFrame
            } else {
                ndx = if (isRunning) 13 + animFrame else 3 + animFrame
            }
        } else {
            ndx = if (facingRight) 10 else 13
        }
        
        super.update()
    }
}
