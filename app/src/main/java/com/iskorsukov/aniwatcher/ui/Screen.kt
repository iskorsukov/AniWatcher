package com.iskorsukov.aniwatcher.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R

sealed class Screen(
    val route: String,
    @StringRes val labelStringId: Int,
    @DrawableRes val iconDrawableId: Int,
    val hasSearchBar: Boolean = true,
    val hasSortingOptions: Boolean = true
) {
    object MediaScreen : Screen(
        "media",
        R.string.media_screen_label,
        R.drawable.ic_outline_video_library_24_white
    )
    object AiringScreen : Screen(
        "airing",
        R.string.airing_screen_label,
        R.drawable.ic_outline_calendar_today_24_white,
        false,
        false
    )
    object FollowingScreen : Screen(
        "following",
        R.string.following_screen_label,
        R.drawable.ic_outline_favorite_border_24_white
    )

    companion object {
        fun ofRoute(route: String): Screen? {
            if (MediaScreen.route == route) {
                return MediaScreen
            }
            if (AiringScreen.route == route) {
                return AiringScreen
            }
            if (FollowingScreen.route == route) {
                return FollowingScreen
            }
            return null
        }
    }
}
