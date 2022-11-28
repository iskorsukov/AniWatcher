package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery

@Entity(tableName = "media")
data class MediaItemEntity(
    @PrimaryKey val mediaId: Int,
    @Embedded val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val bannerImageUrl: String?,
    val mainStudio: String?,
    val popularity: Int?,
    val meanScore: Int?,
    val genresCommaSeparated: String?,
    val siteUrl: String?,
    val nextEpisodeAiringAt: Int?,
    val format: String?
) {
    data class Title(
        val titleRomaji: String?,
        val titleEnglish: String?,
        val titleNative: String?
    )

    companion object {
        fun fromData(data: SeasonAiringDataQuery.Medium): MediaItemEntity {
            return data.run {
                MediaItemEntity(
                    mediaId = id,
                    title = Title(
                        title?.romaji,
                        title?.english,
                        title?.native,
                    ),
                    description = description,
                    coverImageUrl = coverImage?.large,
                    colorStr = coverImage?.color,
                    bannerImageUrl = bannerImage,
                    mainStudio = studios?.studioNode?.firstOrNull()?.name,
                    popularity = popularity,
                    meanScore = meanScore,
                    genresCommaSeparated = genres?.filterNotNull()?.joinToString(separator = ","),
                    siteUrl = siteUrl,
                    nextEpisodeAiringAt = nextAiringEpisode?.airingAt,
                    format = format?.name
                )
            }
        }

        fun fromData(data: RangeAiringDataQuery.Media): MediaItemEntity {
            return data.run {
                MediaItemEntity(
                    mediaId = id,
                    title = Title(
                        title?.romaji,
                        title?.english,
                        title?.native,
                    ),
                    description = description,
                    coverImageUrl = coverImage?.large,
                    colorStr = coverImage?.color,
                    bannerImageUrl = bannerImage,
                    mainStudio = studios?.studioNode?.firstOrNull()?.name,
                    popularity = popularity,
                    meanScore = meanScore,
                    genresCommaSeparated = genres?.filterNotNull()?.joinToString(separator = ","),
                    siteUrl = siteUrl,
                    nextEpisodeAiringAt = nextAiringEpisode?.airingAt,
                    format = format?.name
                )
            }
        }
    }
}