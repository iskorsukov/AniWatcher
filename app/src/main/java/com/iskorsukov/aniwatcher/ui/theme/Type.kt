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

val ContentTextStyleSmallEmphasisWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
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

val ContentTextStyleMediumWhite = TextStyle(
    color = Color.White,
    fontSize = 14.sp
)

val ContentTextStyleMediumEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 14.sp
)

val ContentTextStyleMediumEmphasisWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
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

val PopupButtonTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = Color.White,
    textAlign = TextAlign.Center
)

val PopupButtonTextStyleDark = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = TextColor,
    textAlign = TextAlign.Center
)

data class ThemedTextStyles(
    val category: TextStyle = CategoryTextStyle,
    val contentSmall: TextStyle = ContentTextStyleSmall,
    val contentSmallEmphasis: TextStyle = ContentTextStyleSmallEmphasis,
    val contentSmallEmphasisWhite: TextStyle = ContentTextStyleSmallEmphasisWhite,
    val contentSmallLarger: TextStyle = ContentTextStyleSmallLarger,
    val contentSmallLargerEmphasis: TextStyle = ContentTextStyleSmallLargerEmphasis,
    val contentSmallLargerWhite: TextStyle = ContentTextStyleSmallLargerWhite,
    val contentMedium: TextStyle = ContentTextStyleMedium,
    val contentMediumWhite: TextStyle = ContentTextStyleMediumWhite,
    val contentMediumEmphasis: TextStyle = ContentTextStyleMediumEmphasis,
    val contentMediumEmphasisWhite: TextStyle = ContentTextStyleMediumEmphasisWhite,
    val headline: TextStyle = HeadlineTextStyle,
    val headlineSmall: TextStyle = HeadlineTextStyleSmall,
    val topBarTitle: TextStyle = TopBarTitleTextStyleWhite,
    val popupMessageLabel: TextStyle = ContentTextStyleMediumEmphasisWhite,
    val popupMessageSubLabel: TextStyle = ContentTextStyleMediumWhite,
    val popupMessageButton: TextStyle = PopupButtonTextStyle,
    val onTitleOverlay: TextStyle = ContentTextStyleSmallLargerWhite
) {
    companion object {
        val LIGHT = ThemedTextStyles()

        val DARK = ThemedTextStyles(
            topBarTitle = TopBarTitleTextStyle,
            popupMessageLabel = ContentTextStyleMedium,
            popupMessageSubLabel = ContentTextStyleSmallEmphasis,
            popupMessageButton = PopupButtonTextStyleDark
        )
    }
}

val LocalTextStyles = compositionLocalOf { ThemedTextStyles() }