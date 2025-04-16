package fr.iutlens.mmi.demo.utils

import fr.iutlens.mmi.demo.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.BufferedInputStream
import java.io.InputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@OptIn(ExperimentalResourceApi::class)
actual open class MusicPlayer actual constructor(
    context: Any?,
    resource: String,
    autoplay: Boolean
) {

    init {
        val uri = Res.getUri(resource)
        val resourcePath = uri.substringAfter("!/") // getting the route inside the jar
        val resourceStream: InputStream? = javaClass.getResourceAsStream("/$resourcePath")

        resourceStream?.let {
            val bufferedIn: InputStream = BufferedInputStream(resourceStream) // adding buffer for mark/reset support
            val audioInputStream = AudioSystem.getAudioInputStream(bufferedIn)
            val clip: Clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()
        }
    }

    actual fun start() {
    }

    actual fun pause() {
    }

    actual fun release() {
    }

    actual fun stop(){
    }
}

actual open class SoundPool actual constructor() {
    actual fun load(context: Any?, res: String) {
    }

    actual fun play(
        resource: String,
        leftVolume: Float,
        rightVolume: Float,
        priority: Int,
        loop: Int,
        rate: Float
    ) {
    }
}