package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF283593)
val PrimaryColorDark = Color(0xFF121B30)
val OnPrimaryColor = Color(0xFFFFFFFF)
val OnPrimaryColorDark = Color(0xFF6E859E)
val SecondaryColor = Color(0xFF60EDFF)
val BackgroundColor = Color(0xFFFFFFFF)
val BackgroundColorDark = Color(0xFF13171D)

val TitleOverlayColor = Color(0xE6292929)

val TextColor = Color(0xFF6E859E)

val CardBackgroundColor = Color(0xFFFFFFFF)
val CardBackgroundColorDark = Color(0xFF1F232D)
val CardFooterBackgroundColor = Color(0xFFEFF7FB)
val CardFooterBackgroundColorDark = Color(0xFF191D26)

val CardIndicatorColorRed = Color(0xFFE85D75)
val CardIndicatorColorOrange = Color(0xFFF7BF63)
val CardIndicatorColorGreen = Color(0xFF5DC12F)

val ErrorColor = Color(0xFFFF0000)
val ErrorColorDark = Color(0xFF6F1E1E)
val OnErrorColor = Color(0xFFFFFFFF)
val OnErrorColorDark = Color(0xFFFFC3C3)

data class ThemedColors(
    val primary: Color = PrimaryColor,
    val onPrimary: Color = OnPrimaryColor,
    val background: Color = BackgroundColor,
    val secondary: Color = SecondaryColor,
    val titleOverlay: Color = TitleOverlayColor,
    val text: Color = TextColor,
    val cardBackground: Color = CardBackgroundColor,
    val cardFooterBackground: Color = CardFooterBackgroundColor,
    val cardIndicatorLow: Color = CardIndicatorColorRed,
    val cardIndicatorMedium: Color = CardIndicatorColorOrange,
    val cardIndicatorHigh: Color = CardIndicatorColorGreen,
    val error: Color = ErrorColor,
    val onError: Color = OnErrorColor
) {
    companion object {
        val LIGHT = ThemedColors()

        val DARK = ThemedColors(
            primary = PrimaryColorDark,
            onPrimary = OnPrimaryColorDark,
            background = BackgroundColorDark,
            cardBackground = CardBackgroundColorDark,
            cardFooterBackground = CardFooterBackgroundColorDark,
            error = ErrorColorDark,
            onError = OnErrorColorDark
        )
    }
}

val LocalColors = compositionLocalOf { ThemedColors() }