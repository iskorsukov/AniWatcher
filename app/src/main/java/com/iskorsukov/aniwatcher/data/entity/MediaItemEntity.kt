package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaRankType

@Entity(tableName = "media")
data class MediaItemEntity(
    @PrimaryKey val mediaId: Int,
    @Embedded val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val bannerImageUrl: String?,
    val mainStudio: String?,
    @Embedded val seasonRanking: Ranking?,
    val meanScore: Int?,
    val genresCommaSeparated: String?,
    val siteUrl: String?
) {
    data class Title(
        val titleRomaji: String?,
        val titleEnglish: String?,
        val titleNative: String?
    )

    data class Ranking(
        val rank: Int,
        val season: String
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
                    seasonRanking = rankings?.filterNotNull()?.firstOrNull { ranking ->
                        ranking.type == MediaRankType.POPULAR && ranking.season == data.season
                    }?.run {
                        Ranking(
                            rank,
                            season!!.name
                        )
                    },
                    meanScore = meanScore,
                    genresCommaSeparated = genres?.filterNotNull()?.joinToString(separator = ","),
                    siteUrl = siteUrl
                )
            }
        }
    }
}