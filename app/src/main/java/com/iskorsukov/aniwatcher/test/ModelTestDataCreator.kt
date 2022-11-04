package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem

object ModelTestDataCreator {

    fun baseMediaItem(): MediaItem {
        return MediaItem(
            id = 1,
            title = MediaItem.Title(
                "TitleRomaji",
                "TitleEnglish",
                "TitleNative"
            ),
            description = "Description",
            coverImageUrl = "https://img1.goodfon.ru/original/1024x768/9/d6/kotionok-malysh-vzgliad-trava-boke.jpg",
            colorStr = "#43aee4",
            seasonRanking = MediaItem.Ranking(
                1,
                "FALL"
            ),
            meanScore = 1,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            isFollowing = false
        )
    }

    fun baseAiringScheduleItem(): AiringScheduleItem {
        return AiringScheduleItem(
            1,
            1667833200,
            1,
            baseMediaItem()
        )
    }

    fun baseAiringScheduleItemList(): List<AiringScheduleItem> {
        return listOf(
            baseAiringScheduleItem(),
            baseAiringScheduleItem().id(2).episode(2).airingAt(1667385638),
            baseAiringScheduleItem().id(3).episode(3).airingAt(1667644838),
            baseAiringScheduleItem().id(4).episode(4).airingAt(1667029460)
        )
    }
}

fun MediaItem.emptyTitle(): MediaItem {
    return this.copy(title = MediaItem.Title(
        null,
        null,
        null
    ))
}

fun MediaItem.title(title: MediaItem.Title): MediaItem {
    return this.copy(title = title)
}

fun MediaItem.nullDescription(): MediaItem {
    return this.copy(description = null)
}

fun MediaItem.nullRanking(): MediaItem {
    return this.copy(seasonRanking = null)
}

fun MediaItem.emptyGenres(): MediaItem {
    return this.copy(genres = emptyList())
}

fun MediaItem.nullMeanScore(): MediaItem {
    return this.copy(meanScore = null)
}

fun MediaItem.nullCoverImageUrl(): MediaItem {
    return this.copy(coverImageUrl = null)
}

fun MediaItem.nullColorStr(): MediaItem {
    return this.copy(colorStr = null)
}

fun MediaItem.nullSiteUrl(): MediaItem {
    return this.copy(siteUrl = null)
}

fun MediaItem.isFollowing(isFollowing: Boolean): MediaItem {
    return this.copy(isFollowing = isFollowing)
}

fun AiringScheduleItem.id(id: Int): AiringScheduleItem {
    return this.copy(id = id)
}

fun AiringScheduleItem.episode(episode: Int): AiringScheduleItem {
    return this.copy(episode = episode)
}

fun AiringScheduleItem.airingAt(airingAt: Int): AiringScheduleItem {
    return this.copy(airingAt = airingAt)
}

fun AiringScheduleItem.mediaItem(mediaItem: MediaItem): AiringScheduleItem {
    return this.copy(mediaItem = mediaItem)
}