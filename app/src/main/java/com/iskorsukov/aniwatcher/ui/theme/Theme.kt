package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.iskorsukov.aniwatcher.domain.settings.DarkModeOption

@Composable
fun AniWatcherTheme(darkModeOption: DarkModeOption = DarkModeOption.SYSTEM, content: @Composable () -> Unit) {
    val darkTheme = when (darkModeOption) {
        DarkModeOption.SYSTEM -> isSystemInDarkTheme()
        DarkModeOption.DARK -> true
        DarkModeOption.LIGHT -> false
    }
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