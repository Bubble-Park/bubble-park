package fr.iutlens.mmi.demo.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.sin
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
    private var walkPhase = 0f

    private val walkFrame  = 0
    private val runFrame   = 1
    private val jumpFrame  = 2
    private val fallFrame  = 3

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
        val walkRotation = if (isOnGround) (if (walkPhase % (2 * PI.toFloat()) < PI.toFloat()) 7f else -7f) else 0f
        drawScope.withTransform({
            translate(x, y + 20f)
            rotate(walkRotation, pivot = Offset.Zero)
            if (!facingRight) scale(-1f, 1f, pivot = Offset.Zero)
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
        val constant = 0.2f
        ndx = when {
            !isOnGround -> if (vy < 0) jumpFrame else fallFrame
            speed != 0f && (speed > mapArea.w * constant || speed < -mapArea.w * constant) -> runFrame
            else -> walkFrame
        }

        if (isOnGround && speed != 0f) {
            val phaseSpeed = if (ndx == runFrame) 0.52f else 0.32f
            walkPhase += phaseSpeed
        } else {
            walkPhase = 0f
        }
    }
}
