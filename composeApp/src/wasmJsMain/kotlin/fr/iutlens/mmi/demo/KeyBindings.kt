package fr.iutlens.mmi.demo

import androidx.compose.ui.input.key.Key

// Sur wasmJS, Compose mappe Key.* sur le code physique (KeyboardEvent.code, indépendant du layout).
// Pour un clavier AZERTY :
//   touche 'A' physique = code "KeyQ"  → Key.Q dans Compose
//   touche 'Z' physique = code "KeyW"  → Key.W dans Compose
actual val SHOOT_KEY: Key = Key.Q
actual val JUMP_KEY: Key = Key.W
