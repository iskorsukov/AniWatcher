package com.iskorsukov.aniwatcher.ui.sorting

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.MediaItem

enum class SortingOption(
    @StringRes val labelResId: Int,
    val comparator: Comparator<MediaItem>?
    ) {
    AIRING_AT(R.string.airing_at, null),
    POPULARITY(R.string.popularity, { first, second ->
        val firstRank = first.seasonRanking?.rank ?: Int.MAX_VALUE
        val secondRank = second.seasonRanking?.rank ?: Int.MAX_VALUE
        firstRank - secondRank
    }),
    SCORE(R.string.mean_score, { first, second ->
        val firstScore = first.meanScore ?: 0
        val secondScore = second.meanScore ?: 0
        secondScore - firstScore
    })
}