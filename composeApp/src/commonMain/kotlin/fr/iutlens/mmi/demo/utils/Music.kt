package fr.iutlens.mmi.demo.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lexilabs.basic.sound.Audio
import app.lexilabs.basic.sound.AudioByte
import app.lexilabs.basic.sound.AudioState
import app.lexilabs.basic.sound.ExperimentalBasicSound
import fr.iutlens.mmi.demo.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

expect @Composable fun getContext() : Any?
/**
 * Music permet de jouer de la musique ou des sons pendant le jeu
 *
 */
object Music {

    /**
     * mute permet d'activer ou désactiver le son joué par l'application
     */
    var mute by mutableStateOf(false)

    /**
     * Sound pool gère les bruitages (jusqu'à 10 en simultané ici)
     */
    private val soundPool = AudioByte()

    /**
     * Sound map est un tableau associatif permettant de faire la correspondance
     * entre les id des ressources (R.assets.jungle par exemple) et les
     * id utilisés par soundPool
     */
    private val soundMap = mutableMapOf<String,Any>()

    /**
     * Load sound charge un fichier son (.ogg) pour être joué ensuite à la demande
     *
     * @param context
     * @param id
     */
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun loadSound(res: String){
        soundMap.getOrPut(res) { soundPool.load(getContext() ?: this, Res.getUri(res)) }
    }

    /**
     * Play sound joue un son précédemment chargé (ne fait rien sinon)
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
        soundMap[id]?.let { soundId -> soundPool.play(soundId) }
    }


    @OptIn(ExperimentalBasicSound::class, ExperimentalResourceApi::class)
    @Composable
    operator fun invoke(id: String){
        val context = getContext() ?: this
        val musicPlayer by remember(id){
            mutableStateOf(Audio(context, Res.getUri(id),!mute))
        }
        val audioState by musicPlayer.audioState.collectAsState()
        println(audioState)
        when (audioState) {
            is AudioState.NONE -> musicPlayer.load()
            is AudioState.READY -> if (!mute) musicPlayer.play()
            is AudioState.ERROR -> println((audioState as AudioState.ERROR).message)
            is AudioState.PAUSED -> if (!mute) musicPlayer.play()
            is AudioState.PLAYING -> if (mute) musicPlayer.pause()
            else -> {
                /** DO NOTHING **/
            }
        }
        /*
        DisposableEffect(id, mute) {

            val audioState by musicPlayer.audioState.collectAsState()
            //if (!mute) musicPlayer.play()

            onDispose {
                musicPlayer.stop()
                musicPlayer.release()
            }
        }*/
    }
}