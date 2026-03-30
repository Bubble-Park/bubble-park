package fr.iutlens.mmi.demo.data

import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.environnement_map_sprite

object LevelGenerator {
    fun generate(levelIndex: Int): LevelData = LevelData(
        mapString  = MapGenerator.generate(levelIndex),
        tileSetRes = Res.drawable.environnement_map_sprite,
        startX     = 1.5f,
        startY     = 2.5f,
        mapCode    = MapGenerator.MAP_CODE
    )
}
