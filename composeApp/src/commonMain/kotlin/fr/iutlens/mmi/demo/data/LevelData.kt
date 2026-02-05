package fr.iutlens.mmi.demo.data

import org.jetbrains.compose.resources.DrawableResource

data class LevelData (
    val mapString: String,
    val tileSetRes: DrawableResource,
    val startX: Float,
    val startY: Float,
    val mapCode: String
)