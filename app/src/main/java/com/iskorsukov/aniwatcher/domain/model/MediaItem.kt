package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import java.io.Serializable

data class MediaItem(
    val id: Int,
    val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val bannerImageUrl: String?,
    val mainStudio: String?,
    val seasonRanking: Ranking?,
    val meanScore: Int?,
    val genres: List<String>,
    val siteUrl: String?,
    val nextEpisodeAiringAt: Int?,
    val isFollowing: Boolean
): Serializable {
    data class Title(
        val romaji: String?,
        val english: String?,
        val native: String?
    ): Serializable {
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

        fun containsIgnoreCase(str: String): Boolean {
            return romaji?.contains(str, true) == true ||
                    english?.contains(str, true) == true ||
                    native?.contains(str, true) == true
        }
    }

    data class Ranking(
        val rank: Int,
        val season: String
    ): Serializable

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
                    bannerImageUrl = bannerImageUrl,
                    mainStudio = mainStudio,
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
                    nextEpisodeAiringAt = nextEpisodeAiringAt,
                    isFollowing = followingEntity != null
                )
            }
        }
    }
}
