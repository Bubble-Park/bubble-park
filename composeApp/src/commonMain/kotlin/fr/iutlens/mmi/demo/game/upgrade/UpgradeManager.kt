package fr.iutlens.mmi.demo.game.upgrade

import fr.iutlens.mmi.demo.components.Player
import fr.iutlens.mmi.demo.game.upgrade.upgrades.ExtraLifeUpgrade
import fr.iutlens.mmi.demo.game.upgrade.upgrades.FireRateUpgrade
import fr.iutlens.mmi.demo.game.upgrade.upgrades.MoveSpeedUpgrade
import kotlin.random.Random

class UpgradeManager {

    val catalogue: List<Upgrade> = listOf(
        ExtraLifeUpgrade(),
        FireRateUpgrade(),
        MoveSpeedUpgrade()
    )

    fun getMaxLife(): Int =
        3 + (catalogue.filterIsInstance<ExtraLifeUpgrade>().firstOrNull()?.acquiredCount ?: 0)

    /**
     * Retourne [count] upgrades aléatoires non maxées, sans doublons.
     * Si moins de [count] upgrades sont disponibles, retourne tout ce qui reste.
     */
    fun getRandomCandidates(count: Int = 3): List<Upgrade> =
        catalogue.filter { !it.isMaxed }.shuffled(Random).take(count)

    fun acquire(upgrade: Upgrade, player: Player) {
        upgrade.acquiredCount++
        upgrade.apply(player)
    }

    fun restoreStats(player: Player) = catalogue.forEach { it.restoreStats(player) }

    fun reset() = catalogue.forEach { it.reset() }
}
