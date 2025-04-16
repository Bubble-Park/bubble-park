package fr.iutlens.mmi.demo.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getContext(): Any? = LocalContext.current