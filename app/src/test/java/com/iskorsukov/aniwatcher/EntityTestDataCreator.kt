package com.iskorsukov.aniwatcher

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
            coverImageUrl = "CoverImageMedium",
            colorStr = "CoverImageColor",
            seasonRanking = MediaItemEntity.Ranking(
                1,
                "FALL"
            ),
            meanScore = 1,
            genresSpaceSeparated = "Action Comedy",
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
            airingAt = 1,
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
                    baseAiringScheduleEntity().id(2).episode(2),
                    baseAiringScheduleEntity().id(3).episode(3),
                    baseAiringScheduleEntity().id(4).episode(4)
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
    return this.copy(genresSpaceSeparated = null)
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