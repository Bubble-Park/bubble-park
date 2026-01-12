package fr.iutlens.mmi.demo.geometry

import kotlin.math.floor

class Direction(val di : Int, val dj : Int)

interface Grid {
    fun toArray2DCoordinate(i : Int, j : Int) : Pair<Int,Int>
    fun fromArray2DCoordinate(i :Int, j: Int) : Pair<Int,Int>

    val directions : Array<Direction>
}

object rectangularGrid : Grid {
    override fun toArray2DCoordinate(i: Int, j: Int) = i to j
    override fun fromArray2DCoordinate(i: Int, j: Int) = i to j

    override val directions = arrayOf(
        Direction(1,0),
        Direction(0,-1),
        Direction(-1, 0),
        Direction(0,1)
    )
}

/*
     /\/\/\
    | | | |
    \/\/\/
     | | |
    /\/\/\
   | | | |
 */
object hexagonalGrid : Grid {
    override fun toArray2DCoordinate(i: Int, j: Int) = (i+ floor(j/2.0f).toInt()) to j
    override fun fromArray2DCoordinate(i: Int, j: Int) = (i- floor(j/2.0f).toInt()) to j

    override val directions = arrayOf(
        Direction(1,0),
        Direction(1,-1),
        Direction(0,-1),
        Direction(-1, 0),
        Direction(-1,1),
        Direction(0,1)
    )
}