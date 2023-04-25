package com.iskorsukov.aniwatcher.ui.base.sorting

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem

enum class SortingOption(
    @StringRes val labelResId: Int,
    val comparator: Comparator<Pair<MediaItem, AiringScheduleItem?>>
    ) {
    AIRING_AT(R.string.airing_at, { first, second ->
        val firstAiringAt = first.second?.airingAt ?: Int.MAX_VALUE
        val secondAiringAt = second.second?.airingAt ?: Int.MAX_VALUE
        val diff = firstAiringAt - secondAiringAt
        if (diff == 0) {
            -1
        } else {
            diff
        }
    }),
    POPULARITY(R.string.popularity, { first, second ->
        val firstRank = first.first.popularity ?: 0
        val secondRank = second.first.popularity ?: 0
        val diff = firstRank - secondRank
        if (diff == 0) {
            -1
        } else {
            diff
        }
    }),
    SCORE(R.string.mean_score, { first, second ->
        val firstScore = first.first.meanScore ?: 0
        val secondScore = second.first.meanScore ?: 0
        val diff = secondScore - firstScore
        if (diff == 0) {
            -1
        } else {
            diff
        }
    })
}