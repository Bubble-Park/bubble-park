package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Trex(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph
) : ChaseDino(
    type = DinoType(
        name = "Trex",
        scoreValue = 50,
        ndx = 8,
        behavior = DinoBehavior.ChasePlayer(speed = 12f),
        damagesPlayer = true
    ),
    res, x, y, mapArea, distanceMap, graph
) {
    companion object {
        const val VISUAL_SCALE = 1.5f
        const val HIT_RADIUS = 150f
    }

    override val boundingBox: Rect
        get() = Rect(x - Trex.Companion.HIT_RADIUS, y - Trex.Companion.HIT_RADIUS, x + Trex.Companion.HIT_RADIUS, y + Trex.Companion.HIT_RADIUS)

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x, y)
            scale(Trex.Companion.VISUAL_SCALE, Trex.Companion.VISUAL_SCALE, pivot = Offset.Zero)
            translate(-x, -y)
        }) {
            super.paint(this, elapsed)
        }
    }
}
