package fr.iutlens.mmi.demo.utils

expect open class MusicPlayer(context : Any?, resource : String, autoplay: Boolean = true) {
    fun start()
    fun pause()
    fun stop()
    fun release()
}

expect open class SoundPool(){
    fun load(context: Any?, res: String)
    fun play(resource: String,
             leftVolume: Float,
             rightVolume: Float,
             priority: Int,
             loop: Int,
             rate: Float)
    fun stop(resource: String)
}