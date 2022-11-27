package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val CategoryTextStyle = TextStyle(
    fontSize = 20.sp,
    color = TextColor,
    fontFamily = FontFamily.Monospace
)

val ContentTextStyleSmallLargerWhite = TextStyle(
    color = Color.White,
    fontSize = 14.sp
)

val ContentTextStyleSmall = TextStyle(
    color = TextColor,
    fontSize = 12.sp
)

val ContentTextStyleSmallWhite = TextStyle(
    color = Color.White,
    fontSize = 12.sp
)

val ContentTextStyleSmallEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 12.sp
)

val ContentTextStyleSmallEmphasisWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
    fontSize = 12.sp
)

val ContentTextStyleSmallLarger = TextStyle(
    color = TextColor,
    fontSize = 14.sp
)

val ContentTextStyleSmallLargerEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 14.sp
)

val ContentTextStyleMedium = TextStyle(
    color = TextColor,
    fontSize = 16.sp
)

val ContentTextStyleMediumWhite = TextStyle(
    color = Color.White,
    fontSize = 16.sp
)

val ContentTextStyleMediumEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 16.sp
)

val ContentTextStyleMediumEmphasisWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
    fontSize = 16.sp
)

val HeadlineTextStyle = TextStyle(
    color = TextColor,
    fontSize = 18.sp
)

val HeadlineEmphasisTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 18.sp
)

val TopBarTitleTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 22.sp
)

val TopBarTitleTextStyleWhite = TextStyle(
    fontWeight = FontWeight.Medium,
    color = Color.White,
    fontSize = 22.sp
)

val HeadlineTextStyleSmall = TextStyle(
    color = TextColor,
    fontSize = 14.sp
)

val HeadlineTextStyleSmallEmphasis = TextStyle(
    fontWeight = FontWeight.Medium,
    color = TextColor,
    fontSize = 14.sp
)

val PopupButtonTextStyle = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = Color.White,
    textAlign = TextAlign.Center
)

val PopupButtonTextStyleDark = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = Color.White,
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
    val headlineEmphasis: TextStyle = HeadlineEmphasisTextStyle,
    val headlineSmall: TextStyle = HeadlineTextStyleSmall,
    val headlineSmallEmphasis: TextStyle = HeadlineTextStyleSmallEmphasis,
    val topBarTitle: TextStyle = TopBarTitleTextStyleWhite,
    val popupMessageLabel: TextStyle = ContentTextStyleMediumEmphasisWhite,
    val popupMessageSubLabel: TextStyle = ContentTextStyleMediumWhite,
    val popupMessageButton: TextStyle = PopupButtonTextStyle,
    val onTitleOverlay: TextStyle = ContentTextStyleSmallWhite
) {
    companion object {
        val LIGHT = ThemedTextStyles()

        val DARK = ThemedTextStyles(
            topBarTitle = TopBarTitleTextStyle,
            popupMessageLabel = ContentTextStyleMediumWhite,
            popupMessageSubLabel = ContentTextStyleSmallEmphasisWhite,
            popupMessageButton = PopupButtonTextStyleDark
        )
    }
}

val LocalTextStyles = compositionLocalOf { ThemedTextStyles() }