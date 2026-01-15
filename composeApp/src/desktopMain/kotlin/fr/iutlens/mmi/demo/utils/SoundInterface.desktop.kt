package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.Res
import javazoom.jl.player.JavaSoundAudioDeviceFactory
import javazoom.jl.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
open class SoundResource(val res : String){
    @OptIn(ExperimentalResourceApi::class)
    val path = '/'+ Res.getUri(res).substringAfter("!/")

    init {
        println("res : $res   path : $path   uri:" + Res.getUri(res))
    }

    val stream get() = javaClass.getResourceAsStream(path).buffered()
    val player get() = Player(stream, JavaSoundAudioDeviceFactory().createAudioDevice())
}

actual open class MusicPlayer actual constructor(
    context: Any?,
    resource: String,
    val autoplay: Boolean
) : SoundResource(resource){

    var currentPlayer: Player? = null
    var currentJob: Job? = null

    init { if (autoplay) start() }


    actual fun start() {
        release()
        currentJob = CoroutineScope( Dispatchers.IO).launch {
            do {
                yield()
                currentPlayer = player
                currentPlayer?.play()
            } while(autoplay)
        }.apply {
            invokeOnCompletion{
                currentPlayer = null
                currentJob = null
            }
        }
    }

/** TODO : save actual position **/
    actual fun pause() {
        stop()
    }

    actual fun release() {
        currentJob?.apply {
            if (isActive){
                cancel()
                currentPlayer?.close()
            }
        }
    }

    actual fun stop(){
        release()
    }
}

actual open class SoundPool actual constructor() {

    val map = mutableMapOf<String,SoundResource>()

    actual fun load(context: Any?, res: String) {
        map[res] = SoundResource(res)
    }

    actual fun play(
        resource: String,
        leftVolume: Float,
        rightVolume: Float,
        priority: Int,
        loop: Int,
        rate: Float
    ) {
        val soundResource = map[resource] ?: return
        CoroutineScope( Dispatchers.IO).launch {
            soundResource.player.play()
        }
    }
}