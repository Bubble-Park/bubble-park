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
        behavior = DinoBehavior.ChasePlayer(speed = 15f, hitCount = 3),
        damagesPlayer = true
    ),
    res, x, y, mapArea, distanceMap, graph
) {
    companion object {
        const val VISUAL_SCALE = 2f
        const val HIT_RADIUS = 150f
    }

    override val halfHeight get() = spriteSheet.spriteHeight / 2f * VISUAL_SCALE

    private var _bboxCX = Float.NaN
    private var _bboxCY = Float.NaN
    private var _bbox = Rect.Zero

    override val boundingBox: Rect
        get() {
            if (x != _bboxCX || y != _bboxCY) {
                _bboxCX = x; _bboxCY = y
                _bbox = Rect(x - HIT_RADIUS, y - HIT_RADIUS / 2, x + HIT_RADIUS, y + HIT_RADIUS / 2)
            }
            return _bbox
        }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x, y)
            scale(Trex.Companion.VISUAL_SCALE, Trex.Companion.VISUAL_SCALE, pivot = Offset.Zero)
            translate(-x, -y)
        }) {
            super.paint(this, elapsed)
        }
        drawHitboxIfEnabled(drawScope)
    }
}
