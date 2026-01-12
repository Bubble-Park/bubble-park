package fr.iutlens.mmi.demo.utils

class IntArray2D( val sizeX : Int, val sizeY: Int, val  data : Array<Int>) {
    fun getIndex(x: Int, y: Int) = if (x !in 0..<sizeX ||y  !in 0..<sizeY ) null else x+y*size
    fun getCoord(ndx: Int): Pair<Int, Int> = ndx.mod(sizeX) to (ndx/sizeX)
    val size = sizeY*sizeX
}

fun IntArray2D( sizeX: Int, sizeY: Int, value : Int) = IntArray2D(sizeX,sizeY, Array(sizeY*sizeX){value})
fun IntArray2D( sizeX: Int, sizeY: Int, init : (Int)-> Int) = IntArray2D(sizeX,sizeY, Array(sizeY*sizeX, init))