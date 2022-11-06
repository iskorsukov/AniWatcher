package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity

data class MediaItem(
    val id: Int,
    val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val seasonRanking: Ranking?,
    val meanScore: Int?,
    val genres: List<String>,
    val siteUrl: String?,
    val isFollowing: Boolean
) {
    data class Title(
        val romaji: String?,
        val english: String?,
        val native: String?
    ) {
        fun baseText(): String {
            val orderedTitles = listOf(english, romaji, native)
            val filteredTitles = orderedTitles.filterNotNull()
            return filteredTitles.getOrNull(0) ?: ""
        }

        fun subText(): String {
            val orderedTitles = listOf(english, romaji, native)
            val filteredTitles = orderedTitles.filterNotNull()
            return filteredTitles.getOrNull(1) ?: ""
        }
    }

    data class Ranking(
        val rank: Int,
        val season: String
    )

    companion object {
        fun fromEntity(mediaItemEntity: MediaItemEntity, followingEntity: FollowingEntity?): MediaItem {
            return mediaItemEntity.run {
                MediaItem(
                    id = mediaId,
                    title = Title(
                        title.titleRomaji,
                        title.titleEnglish,
                        title.titleNative
                    ),
                    description = description,
                    coverImageUrl = coverImageUrl,
                    colorStr = colorStr,
                    seasonRanking = seasonRanking?.run {
                        Ranking(
                            rank,
                            season
                        )
                    },
                    meanScore = meanScore,
                    genres = genresCommaSeparated?.split(",")
                        ?.filter { it.isNotEmpty() } ?: emptyList(),
                    siteUrl = siteUrl,
                    isFollowing = followingEntity != null
                )
            }
        }
    }
}
