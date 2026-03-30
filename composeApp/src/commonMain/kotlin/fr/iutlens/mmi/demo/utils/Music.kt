package fr.iutlens.mmi.demo.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


expect @Composable fun getContext() : Any?
object Music {

    /**
     * activer ou désactiver le son joué
     */
    var mute by mutableStateOf(false)

    /**
     * gère les bruitages
     */
    private val soundPool = SoundPool()


    @Composable
    fun loadSound(res: String){
        soundPool.load(getContext(),res)
    }

    /**
     * joue un son (qui a été chargé)
     *
     * @param id
     * @param leftVolume
     * @param rightVolume
     * @param priority
     * @param loop
     * @param rate
     */
    fun playSound(id: String,
                  leftVolume: Float = 1f,
                  rightVolume: Float = 1f,
                  priority: Int = 1,
                  loop: Int = 0,
                  rate: Float = 1f
                  ){
        if (mute) return
        soundPool.play(id, leftVolume,rightVolume,priority,loop,rate)
    }


    @Composable
    operator fun invoke(id: String){
        val context = getContext() ?: this

        DisposableEffect(id, mute) {
            val musicPlayer = MusicPlayer(context, id,!mute)

            onDispose {
                musicPlayer.stop()
                musicPlayer.release()
            }
        }
    }
}