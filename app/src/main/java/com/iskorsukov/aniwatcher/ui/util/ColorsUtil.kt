package com.iskorsukov.aniwatcher.ui.util

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Composable
fun getBackgroundColorForChip(bgColorStr: String?): Color {
    return if (bgColorStr == null) {
        MaterialTheme.colors.primary
    } else {
        Color(android.graphics.Color.parseColor(bgColorStr))
    }
}

// https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
@Composable
fun getContrastTextColorForChip(bgColor: Color): Color {
    val color = bgColor.toArgb()
    // Counting the perceptive luminance - human eye favors green color...
    val luminance: Double =
        1 - (0.299 * android.graphics.Color.red(color) +
                0.587 * android.graphics.Color.green(color) +
                0.114 * android.graphics.Color.blue(color)
                ) / 255

    return if (luminance < 0.5) Color.Black else Color.White
}