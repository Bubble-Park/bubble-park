package fr.iutlens.mmi.demo.components.dino

import androidx.compose.ui.graphics.Color
import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Parasaur(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph
) : FleeDino(
    type = DinoType(
        name = "Parasaur",
        scoreValue = 25,
        ndx = 0,
        behavior = DinoBehavior.FleeFromPlayer()
    ),
    res, x, y, mapArea, distanceMap, graph
)