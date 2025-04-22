package fr.iutlens.mmi.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fr.iutlens.mmi.demo.App
import fr.iutlens.mmi.demo.GameCPreview
import fr.iutlens.mmi.demo.utils.Music.mute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    var muteState : Boolean? = null

    override fun onPause() {
        super.onPause()
        muteState = mute
        mute = true
    }

    override fun onResume() {
        super.onResume()
        mute = muteState ?: return
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

@Preview
@Composable
fun AndroidPreview(){
    GameCPreview()
}