package fr.iutlens.mmi.demo.components

import androidx.compose.ui.graphics.Color
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
) : GenericDino(
    res, x, y, mapArea,
    type = DinoType(
        name = "Trex",
        scoreValue = 50,
        ndx = 8,
        behavior = DinoBehavior.ChasePlayer(speed = 20f),
        damagesPlayer = true,
        color = Color.Red
    ),
    distanceMap = distanceMap,
    graph = graph
)
