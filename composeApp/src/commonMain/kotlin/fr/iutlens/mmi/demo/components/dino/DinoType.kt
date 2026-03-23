package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.graphics.Color

data class DinoType(
    val name: String,
    val scoreValue: Int,
    val ndx: Int,
    val behavior: DinoBehavior,
    val damagesPlayer: Boolean = false,
    val color: Color? = null
)

sealed class DinoBehavior {
    /** Se déplace aléatoirement sur les plateformes. */
    data class Wander(val speed: Float = 11f) : DinoBehavior()

    /** Fuit le joueur quand il est trop proche, erre sinon. */
    data class FleeFromPlayer(
        val walkSpeed: Float = 11f,
        val fleeSpeed: Float = 25f,
        val triggerTiles: Int = 6,
        val releaseTiles: Int = 15
    ) : DinoBehavior()

    /** Fonce vers le joueur en permanence. */
    data class ChasePlayer(val speed: Float = 15f) : DinoBehavior()
}
