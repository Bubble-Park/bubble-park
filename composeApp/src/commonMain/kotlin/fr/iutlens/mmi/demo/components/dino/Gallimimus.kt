package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Gallimimus(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph
) : FleeDino(
    type = DinoType(
        name = "Gallimimus",
        scoreValue = 40,
        ndx = 0,
        behavior = DinoBehavior.FleeFromPlayer(
            walkSpeed = 14f,
            fleeSpeed = 50f,
            triggerTiles = 10,
            releaseTiles = 20
        )
    ),
    res, x, y, mapArea, distanceMap, graph
) {
    companion object {
        const val VISUAL_SCALE = 1.2f
        const val HIT_RADIUS = 65f
        const val VISUAL_Y_OFFSET = 0f
    }

    override val halfHeight get() = spriteSheet.spriteHeight / 2f * VISUAL_SCALE
    override val boundingBox: Rect get() = Rect(x - HIT_RADIUS, y - HIT_RADIUS, x + HIT_RADIUS, y + HIT_RADIUS)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({ translate(0f, VISUAL_Y_OFFSET) }) {
            withTransform({
                translate(x, y)
                scale(VISUAL_SCALE, VISUAL_SCALE, pivot = Offset.Zero)
                translate(-x, -y)
            }) {
                super.paint(this, elapsed)
            }
        }
    }
}
