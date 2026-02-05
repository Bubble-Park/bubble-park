package fr.iutlens.mmi.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.iutlens.mmi.demo.Res
import fr.iutlens.mmi.demo.button_A
import fr.iutlens.mmi.demo.button_B

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    onActionA: (Boolean) -> Unit,
    onActionB: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            modifier = Modifier.size(80.dp),
            image = Res.drawable.button_B,
            onPress = { onActionB(true) },
            onRelease = { onActionB(false) }
        )

        Button(
            modifier = Modifier.size(80.dp),
            image = Res.drawable.button_A,
            onPress = { onActionA(true) },
            onRelease = { onActionA(false) }
        )
    }
}