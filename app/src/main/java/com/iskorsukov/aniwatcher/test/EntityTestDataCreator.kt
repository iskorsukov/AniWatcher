package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity

object EntityTestDataCreator {

    fun mediaItemEntity(
        mediaId: Int,
        title: MediaItemEntity.Title = mediaItemEntityTitle(),
        description: String? = null,
        coverImageUrl: String? = null,
        colorStr: String? = null,
        bannerImageUrl: String? = null,
        mainStudio: String? = null,
        popularity: Int? = null,
        meanScore: Int? = null,
        genresCommaSeparated: String? = null,
        siteUrl: String? = null,
        status: String? = null,
        format: String? = null,
        season: String? = null,
        year: Int? = null
    ): MediaItemEntity {
        return MediaItemEntity(
            mediaId = mediaId,
            title = title,
            description = description,
            coverImageUrl = coverImageUrl,
            colorStr = colorStr,
            bannerImageUrl = bannerImageUrl,
            mainStudio = mainStudio,
            popularity = popularity,
            meanScore = meanScore,
            genresCommaSeparated = genresCommaSeparated,
            siteUrl = siteUrl,
            status = status,
            format = format,
            season = season,
            year = year
        )
    }

    fun mediaItemEntityTitle(
        romaji: String? = null,
        english: String? = null,
        native: String? = null
    ): MediaItemEntity.Title {
        return MediaItemEntity.Title(
            titleRomaji = romaji,
            titleEnglish = english,
            titleNative = native
        )
    }

    fun airingScheduleEntity(
        airingScheduleEntityId: Int,
        airingAt: Int = 1,
        episode: Int = 1,
        mediaItemRelationId: Int
    ): AiringScheduleEntity {
        return AiringScheduleEntity(
            airingScheduleItemId = airingScheduleEntityId,
            airingAt = airingAt,
            episode = episode,
            mediaItemRelationId = mediaItemRelationId
        )
    }

    fun notificationItemEntity(
        notificationItemId: Int? = null,
        firedAtMillis: Long = 1,
        airingScheduleItemRelationId: Int
    ): NotificationItemEntity {
        return NotificationItemEntity(
            notificationItemId = notificationItemId,
            firedAtMillis = firedAtMillis,
            airingScheduleItemRelationId = airingScheduleItemRelationId
        )
    }
}