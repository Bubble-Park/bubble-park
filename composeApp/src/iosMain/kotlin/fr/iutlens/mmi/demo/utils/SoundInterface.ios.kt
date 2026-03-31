package fr.iutlens.mmi.demo.utils

actual open class MusicPlayer actual constructor(
    context: Any?,
    resource: String,
    autoplay: Boolean
) {
    actual fun start() {
    }

    actual fun pause() {
    }

    actual fun stop() {
    }

    actual fun release() {
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

    actual fun stop(resource: String) {
    }
}