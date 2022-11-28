package com.iskorsukov.aniwatcher.ui.util

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow


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

// https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
@Composable
fun getContrastTextColorForChip(bgColorStr: String): Color {
    val r = bgColorStr.substring(1, 3).toInt(16) // hexToR
    val g = bgColorStr.substring(3, 5).toInt(16) // hexToG
    val b = bgColorStr.substring(5, 7).toInt(16) // hexToB
    val uiColors = arrayOf(
        r / 255f,
        g / 255f,
        b / 255f
    )
    val c = uiColors.map { col ->
        if (col <= 0.03928) {
            col / 12.92f
        }
        ((col + 0.055) / 1.055).pow(2.4)
    }
    val luminance = (0.2126 * c[0]) + (0.7152 * c[1]) + (0.0722 * c[2])
    return if (luminance > 0.179) Color.Black else Color.White
}