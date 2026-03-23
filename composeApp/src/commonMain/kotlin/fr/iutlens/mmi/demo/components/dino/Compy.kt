package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Compy(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    graph: PlatformGraph
) : WanderDino(
    type = DinoType(
        name = "Compy",
        scoreValue = 10,
        ndx = 0,
        behavior = DinoBehavior.Wander(speed = 8f)
    ),
    res, x, y, mapArea, graph
) {
    companion object {
        const val VISUAL_SCALE = 1f
    }

    override fun paint(drawScope: DrawScope, elapsed: Long) {
        drawScope.withTransform({
            translate(x, y)
            scale(VISUAL_SCALE, VISUAL_SCALE, pivot = Offset.Zero)
            translate(-x, -y)
        }) {
            super.paint(this, elapsed)
        }
    }
}
