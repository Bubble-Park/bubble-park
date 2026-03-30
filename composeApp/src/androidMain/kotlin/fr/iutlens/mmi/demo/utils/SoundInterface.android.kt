package fr.iutlens.mmi.demo.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import fr.iutlens.mmi.demo.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File

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
    } catch (_: Throwable) { null }

    actual fun start() {
        musicPlayer?.play()
    }

    actual fun pause() {
        musicPlayer?.pause()
    }

    actual fun stop() {
        musicPlayer?.stop()
    }

    actual fun release() {
        musicPlayer?.release()
    }
}

actual open class SoundPool actual constructor() {
    private var nativePool: android.media.SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private val readyIds = mutableSetOf<Int>()
    private val loading = mutableSetOf<String>()

    @OptIn(ExperimentalResourceApi::class)
    actual fun load(context: Any?, res: String) {
        val ctx = context as? Context ?: return
        if (soundIds.containsKey(res) || loading.contains(res)) return
        loading.add(res)
        try {
            if (nativePool == null) {
                nativePool = android.media.SoundPool.Builder()
                    .setMaxStreams(10)
                    .build()
                nativePool!!.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) readyIds.add(sampleId)
                }
            }
        } catch (_: Throwable) {
            loading.remove(res)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = Res.readBytes("files/$res")
                val cacheFile = File(ctx.cacheDir, res)
                cacheFile.writeBytes(bytes)
                withContext(Dispatchers.Main) {
                    val id = nativePool!!.load(cacheFile.absolutePath, 1)
                    soundIds[res] = id
                }
            } catch (_: Throwable) {
                loading.remove(res)
            }
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
        val id = soundIds[resource] ?: return
        if (id !in readyIds) return
        nativePool?.play(id, leftVolume, rightVolume, priority, loop, rate)
    }
}
