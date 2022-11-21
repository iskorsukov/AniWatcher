package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AniWatcherTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val themedColors = if (darkTheme) {
        ThemedColors.DARK
    } else {
        ThemedColors.LIGHT
    }
    val themedTextStyles = if (darkTheme) {
        ThemedTextStyles.DARK
    } else {
        ThemedTextStyles.LIGHT
    }
    CompositionLocalProvider(
        LocalColors provides themedColors,
        LocalTextStyles provides themedTextStyles,
        content = content
    )
}