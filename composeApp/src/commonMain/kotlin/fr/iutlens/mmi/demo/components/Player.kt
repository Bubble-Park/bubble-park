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
) : PhysicsSprite(res, x, y, mapArea, gravity = 5f, jumpForce = -90f) {

    private var frameCounter = 0

    // Définition simple des animations de saut
    private val jumpRisingFrame = 21
    private val jumpFallingFrame = 23

    override fun update() {
        // --- 1. Gestion des Inputs ---
        val position = joystickProvider() ?: return
        
        // Saut : Si joystick vers le haut
        if (position.y < -0.6f) {
            jump()
        }

        // Déplacement Horizontal
        val speed = position.x * mapArea.w / 4
        moveX(speed)

        // --- 2. Physique (Gérée par le parent) ---
        applyPhysics()
        
        // --- 3. Animation ---
        if (!isOnGround) {
            // En l'air
            ndx = if (vy < 0) jumpRisingFrame else jumpFallingFrame
        } else if (!position.isCentered && speed != 0f) {
            // Marche au sol
            frameCounter++
            val animFrame = (frameCounter / 4) % 3
            
            // On peut récupérer maxSpeed du moveX si on voulait être précis,
            // ici on utilise une approximation pour savoir si on court vite
            val maxSpeed = 30f // Valeur locale pour l'animation
            val isRunning = (speed > maxSpeed || speed < -maxSpeed)

            if (speed > 0) {
                ndx = if (isRunning) 10 + animFrame else 0 + animFrame
            } else {
                ndx = if (isRunning) 13 + animFrame else 3 + animFrame
            }
        } else {
            // Immobile au sol
            // Si on était en marche gauche (13..15) ou statique gauche (3) ou si vitesse négative, on reste à gauche
            ndx = if (ndx in 13..15 || ndx == 3 || speed < 0) 13 else 10
        }
        
        // Nécessaire pour mettre à jour la position visuelle dans BasicSprite si elle n'est pas automatique
        // (BasicSprite utilise x et y pour le paint, qui sont modifiés par moveX et applyPhysics)
        super.update()
    }
}
