package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.data.entity.*

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
            siteUrl = "AniListUrl"
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
            id = 1,
            airingAt = 1667833200,
            episode = 1,
            mediaItemRelationId = 1
        )
    }

    fun baseMediaItemWithAiringSchedulesAndFollowingEntity(): MediaItemWithAiringSchedulesAndFollowingEntity {
        return MediaItemWithAiringSchedulesAndFollowingEntity(
            MediaItemWithAiringSchedulesEntity(
                mediaItemEntity = baseMediaItemEntity(),
                airingScheduleEntityList = listOf(
                    baseAiringScheduleEntity(),
                    baseAiringScheduleEntity().id(2).episode(2).airingAt(1667385638),
                    baseAiringScheduleEntity().id(3).episode(3).airingAt(1667644838),
                    baseAiringScheduleEntity().id(4).episode(4).airingAt(1667029460)
                )
            ),
            followingEntity = null
        )
    }
}

fun MediaItemEntity.emptyTitle(): MediaItemEntity {
    return this.copy(title = MediaItemEntity.Title(
        null,
        null,
        null
    ))
}

fun MediaItemEntity.nullDescription(): MediaItemEntity {
    return this.copy(description = null)
}

fun MediaItemEntity.nullRanking(): MediaItemEntity {
    return this.copy(seasonRanking = null)
}

fun MediaItemEntity.nullGenres(): MediaItemEntity {
    return this.copy(genresCommaSeparated = null)
}

fun MediaItemEntity.emptyGenres(): MediaItemEntity {
    return this.copy(genresCommaSeparated = "")
}

fun MediaItemEntity.nullMeanScore(): MediaItemEntity {
    return this.copy(meanScore = null)
}

fun MediaItemEntity.nullCoverImageUrl(): MediaItemEntity {
    return this.copy(coverImageUrl = null)
}

fun MediaItemEntity.nullColorStr(): MediaItemEntity {
    return this.copy(colorStr = null)
}

fun MediaItemEntity.nullSiteUrl(): MediaItemEntity {
    return this.copy(siteUrl = null)
}

fun AiringScheduleEntity.id(id: Int): AiringScheduleEntity {
    return this.copy(id = id)
}

fun AiringScheduleEntity.airingAt(airingAt: Int): AiringScheduleEntity {
    return this.copy(airingAt = airingAt)
}

fun AiringScheduleEntity.episode(episode: Int): AiringScheduleEntity {
    return this.copy(episode = episode)
}

fun AiringScheduleEntity.mediaItemRelationId(mediaItemRelationId: Int): AiringScheduleEntity {
    return this.copy(mediaItemRelationId = mediaItemRelationId)
}

fun MediaItemWithAiringSchedulesAndFollowingEntity.mediaItemEntity(mediaItemEntity: MediaItemEntity): MediaItemWithAiringSchedulesAndFollowingEntity {
    return this.copy(this.mediaItemWithAiringSchedulesEntity.copy(mediaItemEntity = mediaItemEntity))
}

fun MediaItemWithAiringSchedulesAndFollowingEntity.airingScheduleEntityList(airingScheduleEntityList: List<AiringScheduleEntity>): MediaItemWithAiringSchedulesAndFollowingEntity {
    return this.copy(this.mediaItemWithAiringSchedulesEntity.copy(airingScheduleEntityList = airingScheduleEntityList))
}

fun MediaItemWithAiringSchedulesAndFollowingEntity.followingEntity(followingEntity: FollowingEntity?): MediaItemWithAiringSchedulesAndFollowingEntity {
    return this.copy(followingEntity = followingEntity)
}