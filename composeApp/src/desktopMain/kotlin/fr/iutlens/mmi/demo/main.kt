package fr.iutlens.mmi.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.iutlens.mmi.demo.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPTest",
    ) {
        App()
    }
}