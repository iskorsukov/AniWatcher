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

@Composable
fun getContrastTextColorForChip(bgColor: Color): Color {
    val hsvArray = FloatArray(3)
    android.graphics.Color.colorToHSV(bgColor.toArgb(), hsvArray)
    return Color.hsv(
        hsvArray[0],
        if (hsvArray[1] > 0.5) 0f else hsvArray[1],
        if (hsvArray[0] in 40f..200f || hsvArray[1] <= 0.5) 0.4f else 1f
    )
}