package com.iskorsukov.aniwatcher.ui.sorting

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.MediaItem

enum class SortingOption(
    @StringRes val labelResId: Int,
    val comparator: Comparator<MediaItem>
    ) {
    AIRING_AT(R.string.airing_at, { first, second ->
        val firstAiringAt = first.nextEpisodeAiringAt ?: Int.MAX_VALUE
        val secondAiringAt = second.nextEpisodeAiringAt ?: Int.MAX_VALUE
        val diff = firstAiringAt - secondAiringAt
        if (diff == 0){
            1
        } else {
            diff
        }
    }),
    POPULARITY(R.string.popularity, { first, second ->
        val firstRank = first.seasonRanking?.rank ?: Int.MAX_VALUE
        val secondRank = second.seasonRanking?.rank ?: Int.MAX_VALUE
        val diff = firstRank - secondRank
        if (diff == 0){
            1
        } else {
            diff
        }
    }),
    SCORE(R.string.mean_score, { first, second ->
        val firstScore = first.meanScore ?: 0
        val secondScore = second.meanScore ?: 0
        val diff = secondScore - firstScore
        if (diff == 0){
            1
        } else {
            diff
        }
    })
}