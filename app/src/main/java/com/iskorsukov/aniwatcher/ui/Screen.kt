package com.iskorsukov.aniwatcher.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R

sealed class Screen(
    val route: String,
    @StringRes val labelStringId: Int,
    @StringRes val thisWeekLabelStringId: Int? = null,
    @DrawableRes val iconDrawableId: Int,
    val hasSearchBar: Boolean = true,
    val hasSeasonYear: Boolean = false
) {
    object MediaScreen : Screen(
        route = "media",
        labelStringId = R.string.media_screen_season_label,
        thisWeekLabelStringId = R.string.media_screen_label,
        iconDrawableId = R.drawable.ic_outline_video_library_24_white,
        hasSeasonYear = true
    )

    object AiringScreen : Screen(
        route = "airing",
        labelStringId = R.string.airing_screen_label,
        iconDrawableId = R.drawable.ic_outline_calendar_today_24_white,
        hasSearchBar = false,
        hasSeasonYear = true
    )

    object FollowingScreen : Screen(
        route = "following",
        labelStringId = R.string.following_screen_label,
        iconDrawableId = R.drawable.ic_outline_favorite_border_24_white
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
