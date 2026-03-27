package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.iutlens.mmi.demo.Joystick
import fr.iutlens.mmi.demo.JoystickPosition

@Composable
fun Controllers(
    modifier: Modifier = Modifier,
    onJoystickChange: (JoystickPosition?) -> Unit,
    onActionA: (Boolean) -> Unit = {},
    onActionB: (Boolean) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val minDim = minOf(maxWidth, maxHeight)
        val joystickSize = minDim * 0.32f
        val bottomPadding = maxHeight * 0.08f
        val sidePadding = maxWidth * 0.04f

        Joystick(
            modifier = Modifier
                .size(joystickSize)
                .align(Alignment.BottomStart)
                .padding(start = sidePadding, bottom = bottomPadding),
            onChange = onJoystickChange
        )

        Buttons(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = sidePadding, bottom = bottomPadding),
            onActionA = onActionA,
            onActionB = onActionB
        )
    }
}
