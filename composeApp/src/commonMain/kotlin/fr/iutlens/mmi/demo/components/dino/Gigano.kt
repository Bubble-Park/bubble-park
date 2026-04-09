package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Gigano(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph,
    speed: Float = 5f,
    hitCount: Int = 10
) : ChaseDino(
    type = DinoType(
        name = "giganotosaurus",
        scoreValue = 500,
        ndx = 0,
        behavior = DinoBehavior.ChasePlayer(speed = speed, hitCount = hitCount),
        damagesPlayer = true
    ),
    res, x, y, mapArea, distanceMap, graph
) {
    companion object {
        const val VISUAL_SCALE = 2f
        const val HIT_HALF_X = 350f
        const val HIT_HALF_Y = 200f
    }

    override val isStunImmune: Boolean = true

    override val halfHeight get() = spriteSheet.spriteHeight / 2f * VISUAL_SCALE

    private var _bboxCX = Float.NaN
    private var _bboxCY = Float.NaN
    private var _bbox = Rect.Zero

    override val boundingBox: Rect
        get() {
            if (x != _bboxCX || y != _bboxCY) {
                _bboxCX = x; _bboxCY = y
                _bbox = Rect(x - HIT_HALF_X, y - HIT_HALF_Y, x + HIT_HALF_X, y + HIT_HALF_Y)
            }
            return _bbox
        }

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
