package fr.iutlens.mmi.demo.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        get() = if (invincibilityFrames > 0 && invincibilityFrames % 8 < 4) 0.2f else 1f
    private val INVINCIBILITY_DURATION = 120

    // Variables d'animation
    private var frameCounter = 0
    private var facingRight = true

    // Constantes d'animation
    private val jumpRisingFrame = 21
    private val jumpFallingFrame = 23
    private val jumpRisingFrameLeft = 26
    private val jumpFallingFrameLeft = 28

    private val walkRightStart = 0
    private val walkLeftStart = 3
    private val runRightStart = 10
    private val runLeftStart = 13

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
        frameCounter = 0
    }

    override fun update() {
        // Gestion de l'invulnérabilité
        if (invincibilityFrames > 0) {
            invincibilityFrames--
        }

        if (isDead) {
            // ndx = deadFrame
            super.update()
            return
        }

        // Logique de déplacement
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

        // Logique d'animation
        updateAnimationState(speed, position)

        super.update()
    }

    /**
     * Met à jour l'état d'animation en fonction de la position du joystick et de la vitesse du joueur
     * @param speed Vitesse du joueur
     * @param position Position du joystick
     */
    private fun updateAnimationState(speed: Float, position: JoystickPosition) {
        if (!isOnGround) {
            // Le joueur est en l'air
            if (facingRight) {
                ndx = if (vy < 0) jumpRisingFrame else jumpFallingFrame
            } else {
                ndx = if (vy < 0) jumpRisingFrameLeft else jumpFallingFrameLeft
            }
        } else if (!position.isCentered && speed != 0f) {
            // Au sol et en mouvement
            frameCounter++
            val animFrame = (frameCounter / 4) % 3

            val isRunning = (speed > mapArea.w * 0.6f || speed < -mapArea.w * 0.6f)

            if (facingRight) {
                ndx = (if (isRunning) runRightStart else walkRightStart) + animFrame
            } else {
                ndx = (if (isRunning) runLeftStart else walkLeftStart) + animFrame
            }
        } else {
            ndx = if (facingRight) walkRightStart else walkLeftStart
        }
    }
}