package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val CategoryTextStyle = TextStyle(
    fontSize = 18.sp,
    color = TextColor,
    fontFamily = FontFamily.Monospace
)

val ContentTextStyleSmallLargerWhite = TextStyle(
    color = Color.White,
    fontSize = 12.sp
)

val ContentTextStyleSmall = TextStyle(
    color = TextColor,
    fontSize = 10.sp
)

val ContentTextStyleSmallEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 10.sp
)

val ContentTextStyleSmallLarger = TextStyle(
    color = TextColor,
    fontSize = 12.sp
)

val ContentTextStyleSmallLargerEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 12.sp
)

val ContentTextStyleMedium = TextStyle(
    color = TextColor,
    fontSize = 14.sp
)

val ContentTextStyleMediumEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 14.sp
)

val HeadlineTextStyle = TextStyle(
    color = TextColor,
    fontSize = 16.sp
)

val TopBarTitleTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 20.sp
)

val TopBarTitleTextStyleWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
    fontSize = 20.sp
)

val HeadlineTextStyleSmall = TextStyle(
    color = TextColor,
    fontSize = 12.sp
)

val ErrorLabelTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = OnErrorColor
)

val ErrorSubLabelTextStyle = TextStyle(
    fontSize = 14.sp,
    color = OnErrorColor
)

val ErrorButtonTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = OnErrorColor,
    textAlign = TextAlign.Center
)

val ErrorLabelTextStyleDark = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = OnErrorColorDark
)

val ErrorSubLabelTextStyleDark = TextStyle(
    fontSize = 14.sp,
    color = OnErrorColorDark
)

val ErrorButtonTextStyleDark = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = OnErrorColorDark,
    textAlign = TextAlign.Center
)

data class ThemedTextStyles(
    val category: TextStyle = CategoryTextStyle,
    val contentSmall: TextStyle = ContentTextStyleSmall,
    val contentSmallEmphasis: TextStyle = ContentTextStyleSmallEmphasis,
    val contentSmallLarger: TextStyle = ContentTextStyleSmallLarger,
    val contentSmallLargerEmphasis: TextStyle = ContentTextStyleSmallLargerEmphasis,
    val contentSmallLargerWhite: TextStyle = ContentTextStyleSmallLargerWhite,
    val contentMedium: TextStyle = ContentTextStyleMedium,
    val contentMediumEmphasis: TextStyle = ContentTextStyleMediumEmphasis,
    val headline: TextStyle = HeadlineTextStyle,
    val headlineSmall: TextStyle = HeadlineTextStyleSmall,
    val topBarTitle: TextStyle = TopBarTitleTextStyleWhite,
    val errorLabel: TextStyle = ErrorLabelTextStyle,
    val errorSubLabel: TextStyle = ErrorSubLabelTextStyle,
    val errorButton: TextStyle = ErrorButtonTextStyle,
    val onTitleOverlay: TextStyle = ContentTextStyleSmallLargerWhite
) {
    companion object {
        val LIGHT = ThemedTextStyles()

        val DARK = ThemedTextStyles(
            topBarTitle = TopBarTitleTextStyle,
            errorLabel = ErrorLabelTextStyleDark,
            errorSubLabel = ErrorSubLabelTextStyleDark,
            errorButton = ErrorButtonTextStyleDark
        )
    }
}

val LocalTextStyles = compositionLocalOf { ThemedTextStyles() }