package fr.iutlens.mmi.demo.game
data class Stats (
    val damage: Int,
    val health: Int
)

data class Dino (
    val id: Int,
    val nom: String,
    val scoreValue: Int = 10,
    val stats: Stats
)

class Score (var score: Int = 0){
    val stego = Dino(
        id = 0,
        nom = "Stégosaure",
        scoreValue = 50,
        stats = Stats(
            damage = 1,
            health = 100
        )
    )

    fun get(): Int {
        return score
    }

    fun add(dino: Dino) {
        score += dino.scoreValue;
    }
}