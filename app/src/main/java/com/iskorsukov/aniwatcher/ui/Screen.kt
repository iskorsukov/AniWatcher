package com.iskorsukov.aniwatcher.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R

sealed class Screen(
    val route: String,
    @StringRes val labelStringId: Int,
    @DrawableRes val iconDrawableId: Int) {
    object AiringScreen : Screen(
        "airing",
        R.string.airing_screen_label,
        R.drawable.ic_outline_calendar_today_24_white
    )
    object FollowingScreen : Screen(
        "following",
        R.string.following_screen_label,
        R.drawable.ic_outline_favorite_border_24_white
    )
}
