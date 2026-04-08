package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Stegosaurus(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph
) : DefensiveDino(
    type = DinoType(
        name = "Stegosaurus",
        scoreValue = 30,
        ndx = 0,
        behavior = DinoBehavior.Defensive(),
        damagesPlayer = true
    ),
    res, x, y, mapArea, distanceMap, graph
) {
    companion object {
        const val VISUAL_SCALE = 2f
        const val HIT_RADIUS = 115f
        const val VERTICAL_OFFSET = 15f
    }

    override val halfHeight get() = spriteSheet.spriteHeight / 2f * VISUAL_SCALE - VERTICAL_OFFSET
    override val hitRadius: Float get() = HIT_RADIUS

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x, y)
            scale(VISUAL_SCALE, VISUAL_SCALE, pivot = Offset.Zero)
            translate(-x, -y)
        }) {
            super.paint(this, elapsed)
        }
        drawHitboxIfEnabled(drawScope)
    }
}
