package fr.iutlens.mmi.demo

import androidx.compose.ui.input.key.Key

// Sur JVM/desktop, Compose mappe Key.* sur le caractère produit (layout-dépendant).
// Sur AZERTY, Key.A = touche 'A', Key.Z = touche 'Z' → correct.
actual val SHOOT_KEY: Key = Key.A
actual val JUMP_KEY: Key = Key.Z
