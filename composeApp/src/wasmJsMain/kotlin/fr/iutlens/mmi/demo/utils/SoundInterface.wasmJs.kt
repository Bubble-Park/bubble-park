package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.Res
import kotlinx.browser.document
import kotlinx.dom.appendElement
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.Audio
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event
import kotlin.contracts.ExperimentalContracts

@OptIn(ExperimentalResourceApi::class,
    kotlin.contracts.ExperimentalContracts::class)
actual open class MusicPlayer actual constructor(
    context: Any?,
    resource: String,
    autoplay: Boolean
) {
    val resId = "music-$resource"

    private fun getPlayerElement(): HTMLAudioElement? {
        return document.getElementById(resId) as? HTMLAudioElement
    }

    init {
        release()
        document.body?.appendElement("audio") {
            this as HTMLAudioElement
            this.id = resId
            this.src = Res.getUri("files/$resource")
            this.loop = autoplay
            this.muted = true  // nécessaire pour que l'autoplay soit accepté par le navigateur
        }
        if (autoplay) start()
    }

    actual fun start() {
        val audio = getPlayerElement() ?: return
        audio.play()  // accepté car muted = true
        audio.muted = false  // fonctionne si le navigateur (MEI) le permet, sinon reste muté
        // Sur premier geste utilisateur : démuté garanti
        val unmuteHandler: (Event) -> Unit = { audio.muted = false }
        document.addEventListener("click",      unmuteHandler)
        document.addEventListener("keydown",    unmuteHandler)
        document.addEventListener("touchstart", unmuteHandler)
    }

    actual fun pause() {
        getPlayerElement()?.pause()
    }

    actual fun release() {
        val playerEl = getPlayerElement()
        playerEl?.pause()
        playerEl?.remove()
    }

    actual fun stop(){
        release()
    }
}

actual open class SoundPool actual constructor() {

    val map = mutableMapOf<String, Audio>()
    private val activeClones = mutableMapOf<String, Audio>()

    @OptIn(ExperimentalContracts::class, ExperimentalResourceApi::class)
    actual fun load(context: Any?, res: String) {
        if(map.containsKey(res)) return
        map[res] = document.body?.appendElement("audio") {
            this as HTMLAudioElement
            this.id = "sound-$res"
            this.src = Res.getUri("files/$res")
            this.load()
        } as Audio
    }

    actual fun play(
        resource: String,
        leftVolume: Float,
        rightVolume: Float,
        priority: Int,
        loop: Int,
        rate: Float
    ) {
        activeClones[resource]?.pause()
        activeClones.remove(resource)
        (map[resource]?.cloneNode(true) as? Audio)?.apply {
            activeClones[resource] = this
            onended = { activeClones.remove(resource); remove() }
            playbackRate = rate.toDouble()
            this.volume = (leftVolume + rightVolume) / 2.0
            play()
        }
    }

    actual fun stop(resource: String) {
        activeClones.remove(resource)?.pause()
    }
}