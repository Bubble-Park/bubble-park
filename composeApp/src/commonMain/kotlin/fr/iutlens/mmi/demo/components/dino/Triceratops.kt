package fr.iutlens.mmi.demo.components.dino

import fr.iutlens.mmi.demo.game.sprite.TiledArea
import fr.iutlens.mmi.demo.utils.DistanceMap
import fr.iutlens.mmi.demo.utils.PlatformGraph
import org.jetbrains.compose.resources.DrawableResource

class Triceratops(
    res: DrawableResource,
    x: Float, y: Float,
    mapArea: TiledArea,
    distanceMap: DistanceMap,
    graph: PlatformGraph
) : DefensiveDino(
    type = DinoType(
        name = "Triceratops",
        scoreValue = 35,
        ndx = 0,
        behavior = DinoBehavior.Defensive(),
        damagesPlayer = true
    ),
    res, x, y, mapArea, distanceMap, graph
)
