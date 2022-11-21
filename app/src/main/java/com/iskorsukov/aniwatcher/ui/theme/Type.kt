package com.iskorsukov.aniwatcher.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val CategoryTextStyle = TextStyle(
    fontSize = 18.sp,
    color = TextColorLight,
    fontFamily = FontFamily.Monospace
)

val ContentTextStyleSmallLargerWhite = TextStyle(
    color = Color.White,
    fontSize = 12.sp
)

val ContentTextStyleSmall = TextStyle(
    color = TextColorLight,
    fontSize = 10.sp
)

val ContentTextStyleSmallEmphasis = TextStyle(
    fontWeight = FontWeight.Bold,
    color = TextColorLight,
    fontSize = 10.sp
)

val ContentTextStyleSmallLarger = TextStyle(
    color = TextColorLight,
    fontSize = 12.sp
)

val ContentTextStyleMedium = TextStyle(
    color = TextColorLight,
    fontSize = 14.sp
)

val ContentTextStyleMediumEmphasis = TextStyle(
    fontWeight = FontWeight.Bold,
    color = TextColorLight,
    fontSize = 14.sp
)

val HeadlineTextStyle = TextStyle(
    color = TextColorLight,
    fontSize = 16.sp
)

val HeadlineTextStyleSmall = TextStyle(
    color = TextColorLight,
    fontSize = 12.sp
)

val ErrorLabelTextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    color = Color.White
)

val ErrorSubLabelTextStyle = TextStyle(
    fontSize = 14.sp,
    color = Color.White
)

val ErrorButtonTextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    color = Color.White,
    textAlign = TextAlign.Center
)