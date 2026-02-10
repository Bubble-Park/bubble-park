package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Joystick
import fr.iutlens.mmi.demo.JoystickPosition

@Composable
fun Controllers(
    modifier: Modifier = Modifier,
    onJoystickChange: (JoystickPosition) -> Unit,
    onActionA: (Boolean) -> Unit = {},
    onActionB: (Boolean) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Joystick(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .padding(32.dp),
            onChange = onJoystickChange
        )

        Buttons(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            onActionA = onActionA,
            onActionB = onActionB
        )
    }
}
