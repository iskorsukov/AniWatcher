package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.NotificationItemEntity

object EntityTestDataCreator {
    fun baseMediaItemEntity(): MediaItemEntity {
        return MediaItemEntity(
            mediaId = 1,
            title = MediaItemEntity.Title(
                "TitleRomaji",
                "TitleEnglish",
                "TitleNative"
            ),
            description = "Description",
            coverImageUrl = "https://img1.goodfon.ru/original/1024x768/9/d6/kotionok-malysh-vzgliad-trava-boke.jpg",
            colorStr = "#43aee4",
            bannerImageUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/139587-XFbotQOPcLC4.jpg",
            mainStudio = "Studio",
            seasonRanking = MediaItemEntity.Ranking(
                1,
                "FALL"
            ),
            meanScore = 1,
            genresCommaSeparated = "Action,Comedy",
            nextEpisodeAiringAt = 1667833200,
            siteUrl = "AniListUrl",
            format = "TV"
        )
    }

    fun baseFollowingEntity(): FollowingEntity {
        return FollowingEntity(
            1,
            1
        )
    }

    fun baseAiringScheduleEntity(): AiringScheduleEntity {
        return AiringScheduleEntity(
            airingScheduleItemId = 1,
            airingAt = 1667833200,
            episode = 1,
            mediaItemRelationId = 1
        )
    }

    fun baseAiringScheduleEntityList(): List<AiringScheduleEntity> {
        return listOf(
            baseAiringScheduleEntity(),
            baseAiringScheduleEntity().id(2).episode(2).airingAt(1667385638),
            baseAiringScheduleEntity().id(3).episode(3).airingAt(1667644838),
            baseAiringScheduleEntity().id(4).episode(4).airingAt(1667029460)
        )
    }

    fun baseNotificationEntity(): NotificationItemEntity {
        return NotificationItemEntity(
            1,
            1667833200000L,
            1
        )
    }
}

fun AiringScheduleEntity.id(id: Int): AiringScheduleEntity {
    return this.copy(airingScheduleItemId = id)
}

fun AiringScheduleEntity.airingAt(airingAt: Int): AiringScheduleEntity {
    return this.copy(airingAt = airingAt)
}

fun AiringScheduleEntity.episode(episode: Int): AiringScheduleEntity {
    return this.copy(episode = episode)
}