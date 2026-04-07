package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.button_A
import fr.iutlens.mmi.demo.button_B

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    minDim: Dp = 300.dp,
    onActionA: (Boolean) -> Unit,
    onActionB: (Boolean) -> Unit
) {
    val buttonSize = minDim * 0.22f
    val staggerOffset = minDim * 0.07f

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(minDim * 0.03f)
    ) {
        Button(
            modifier = Modifier.size(buttonSize),
            image = Res.drawable.button_B,
            onPress = { onActionB(true) },
            onRelease = { onActionB(false) }
        )

        Button(
            modifier = Modifier.size(buttonSize).offset(y = -staggerOffset),
            image = Res.drawable.button_A,
            onPress = { onActionA(true) },
            onRelease = { onActionA(false) }
        )
    }
}