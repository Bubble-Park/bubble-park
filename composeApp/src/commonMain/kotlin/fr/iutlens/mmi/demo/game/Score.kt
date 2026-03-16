package fr.iutlens.mmi.demo.game
import fr.iutlens.mmi.demo.components.Dino

class Score (var score: Int = 0){

    fun get(): Int {
        return score
    }

    fun add(dino: Dino) {
        // score += dino.scoreValue;
    }
}