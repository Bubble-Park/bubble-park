package fr.iutlens.mmi.demo

import androidx.compose.ui.input.key.Key

/** Touche de tir (dépend du layout clavier par plateforme). */
expect val SHOOT_KEY: Key

/** Touche de saut (dépend du layout clavier par plateforme). */
expect val JUMP_KEY: Key
