package fr.iutlens.mmi.demo.game

class Score(var score: Int = 0) {

    fun get(): Int {
        return score
    }

    fun add(points: Int) {
        score += points
    }
}
