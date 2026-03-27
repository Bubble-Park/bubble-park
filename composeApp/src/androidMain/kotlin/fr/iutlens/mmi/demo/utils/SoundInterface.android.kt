package fr.iutlens.mmi.demo.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import fr.iutlens.mmi.demo.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi

@UnstableApi
actual open class MusicPlayer actual constructor(
    context: Any?,
    resource: String,
    autoplay: Boolean
) {
    @OptIn(ExperimentalResourceApi::class)
    private val musicPlayer = try {
        ExoPlayer.Builder(context as Context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    ResolvingByteArrayDataSource.Factory { uri ->
                        runBlocking { Res.readBytes(uri.path!!) }
                    }
                )
            )
            .build()?.apply {
                setMediaItem(MediaItem.fromUri(resource))
                prepare()
                if (autoplay) {
                    play()
                    setRepeatMode(Player.REPEAT_MODE_ALL)
                }
            }
    } catch (_: Exception) { null }

    actual fun start() {
        musicPlayer?.play()
    }

    actual fun pause() {
        musicPlayer?.pause()
    }

    actual fun stop(){
        musicPlayer?.stop()
    }

    actual fun release() {
        musicPlayer?.release()
    }
}

actual open class SoundPool actual constructor() {
    val map = mutableMapOf<String,MediaItem>()
    var pool : Array<Player>? = null

    /*(10){
        ExoPlayer.Builder(context as Context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    ResolvingByteArrayDataSource.Factory { uri ->
                        runBlocking { Res.readBytes(uri.path!!) }
                    }
                )
            )
            .build()
    }
    */
    @UnstableApi
    @OptIn(ExperimentalResourceApi::class)
    actual fun load(context: Any?, res: String) {
        try {
            if (pool == null) {
                pool = Array(10) {
                    ExoPlayer.Builder(context as Context)
                        .setMediaSourceFactory(
                            DefaultMediaSourceFactory(
                                ResolvingByteArrayDataSource.Factory { uri ->
                                    runBlocking { Res.readBytes(uri.path!!) }
                                }
                            )
                        )
                        .build()
                }
            }
            map[res] = MediaItem.fromUri(res)
        } catch (_: Exception) {
            // Environnement preview ou contexte invalide, on ignore
        }
    }

    actual fun play(
        resource: String,
        leftVolume: Float,
        rightVolume: Float,
        priority: Int,
        loop: Int,
        rate: Float
    ) {
        val media = map[resource] ?: return
        pool?.firstOrNull { !it.isPlaying }?.apply {
            this.volume = (leftVolume+rightVolume)/2
            setMediaItem(media)
            prepare()
            play()
        }
    }
}