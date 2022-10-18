package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.WeekAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaRankType

data class MediaItem(
    val id: Int,
    val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val seasonRanking: Ranking?,
    val meanScore: Int,
    val genres: List<String>,
    val siteUrl: String?
) {
    data class Title(
        val romaji: String?,
        val english: String?,
        val native: String?
    )

    data class Ranking(
        val rank: Int,
        val season: String?
    )

    companion object {
        const val NO_MEAN_SCORE = -1

        fun fromData(data: WeekAiringDataQuery.Media): MediaItem {
            return data.run {
                MediaItem(
                    id = id,
                    title = Title(
                        title?.romaji,
                        title?.english,
                        title?.native
                    ),
                    description = description,
                    coverImageUrl = coverImage?.medium,
                    colorStr = coverImage?.color,
                    seasonRanking = rankings?.filterNotNull()?.first { ranking ->
                        ranking.type == MediaRankType.POPULAR && ranking.season != null
                    }?.run {
                          Ranking(
                              rank,
                              season!!.name
                          )
                    },
                    meanScore = meanScore ?: NO_MEAN_SCORE,
                    genres = genres?.filterNotNull() ?: emptyList(),
                    siteUrl = siteUrl
                )
            }
        }
    }
}
