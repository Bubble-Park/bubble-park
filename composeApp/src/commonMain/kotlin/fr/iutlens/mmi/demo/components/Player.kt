package fr.iutlens.mmi.demo.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.JoystickPosition
import fr.iutlens.mmi.demo.game.sprite.PhysicsSprite
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import org.jetbrains.compose.resources.DrawableResource

class Player(
    res: DrawableResource,
    x: Float,
    y: Float,
    mapArea: TiledArea,
    val joystickProvider: () -> JoystickPosition?,
    val jumpActionProvider: () -> Boolean
) : PhysicsSprite(res, x, y, mapArea, gravity = 5.5f, jumpForce = -64f) {

    // Variables de vie
    private var _life by mutableStateOf(3)
    val life: Int
        get() = _life
    val isDead: Boolean
        get() = _life <= 0
    private var invincibilityFrames = 0

    override val paintAlpha: Float
        get() = if (invincibilityFrames > 0 && invincibilityFrames % 8 < 4) 0.6f else 1f
    private val INVINCIBILITY_DURATION = 120

    // Variables d'animation
    private var facingRight = true

    // Indices dans le spritesheet 2x2
    private val walkFrame  = 0  // (0,0) top-left
    private val runFrame   = 1  // (1,0) top-right
    private val jumpFrame  = 2  // (0,1) bottom-left
    private val fallFrame  = 3  // (1,1) bottom-right

    var lastAngle = 0.0

    /**
     * Prend des dégâts et déclenche l'invulnérabilité
     * @return true si le joueur est mort après le hit
     */
    fun takeDamage(): Boolean {
        if (invincibilityFrames > 0 || isDead) return false
        _life--
        if (_life < 0) _life = 0
        invincibilityFrames = INVINCIBILITY_DURATION
        return true
    }

    /**
     * Donne 1 vie au joueur si possible
     */
    fun heal() {
        if (_life < 3) _life++
    }

    override fun reset(x: Float, y: Float) {
        super.reset(x, y)
        invincibilityFrames = 0
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        val w2 = spriteSheet.spriteWidth / 2
        val h2 = spriteSheet.spriteHeight / 2
        drawScope.withTransform({
            translate(x, y)
            if (!facingRight) scale(-1f, 1f)
        }) {
            spriteSheet.paint(this, ndx, -w2, -h2, alpha = paintAlpha)
        }
    }

    override fun update() {
        if (invincibilityFrames > 0) {
            invincibilityFrames--
        }

        if (isDead) {
            super.update()
            return
        }

        val position = joystickProvider() ?: JoystickPosition.centered

        if (!position.isCentered) {
            lastAngle = position.angle
        }

        if (jumpActionProvider()) {
            jump()
        }

        val speed = position.x * mapArea.w / 4
        if (speed > 0) facingRight = true
        if (speed < 0) facingRight = false

        moveX(speed, 60f)
        applyPhysics()

        updateAnimationState(speed)

        super.update()
    }

    private fun updateAnimationState(speed: Float) {
        ndx = when {
            !isOnGround -> if (vy < 0) jumpFrame else fallFrame
            speed != 0f && (speed > mapArea.w * 0.6f || speed < -mapArea.w * 0.6f) -> runFrame
            else -> walkFrame
        }
    }
}
