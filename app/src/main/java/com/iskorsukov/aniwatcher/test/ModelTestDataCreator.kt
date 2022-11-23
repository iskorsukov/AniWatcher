package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem

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
            bannerImageUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/139587-XFbotQOPcLC4.jpg",
            mainStudio = "Studio",
            seasonRanking = MediaItem.Ranking(
                1,
                "FALL"
            ),
            meanScore = 1,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            nextEpisodeAiringAt = 1667833200,
            format = MediaItem.LocalFormat.TV,
            isFollowing = false
        )
    }

    fun baseAiringScheduleItem(isFollowing: Boolean = false): AiringScheduleItem {
        return AiringScheduleItem(
            1,
            1667833200,
            1,
            baseMediaItem().isFollowing(isFollowing)
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

    fun baseNotificationItem(): NotificationItem {
        return NotificationItem(
            1,
            1667833200000L,
            baseMediaItem(),
            baseAiringScheduleItem()
        )
    }
}

fun MediaItem.id(id: Int): MediaItem {
    return this.copy(id = id)
}

fun MediaItem.title(title: MediaItem.Title): MediaItem {
    return this.copy(title = title)
}

fun MediaItem.meanScore(meanScore: Int?): MediaItem {
    return this.copy(meanScore = meanScore)
}

fun MediaItem.nextEpisodeAiringAt(nextEpisodeAiringAt: Int): MediaItem {
    return this.copy(nextEpisodeAiringAt = nextEpisodeAiringAt)
}

fun MediaItem.description(description: String): MediaItem {
    return this.copy(description = description)
}

fun MediaItem.ranking(seasonRanking: MediaItem.Ranking?): MediaItem {
    return this.copy(seasonRanking = seasonRanking)
}

fun MediaItem.isFollowing(isFollowing: Boolean): MediaItem {
    return this.copy(isFollowing = isFollowing)
}

fun MediaItem.bannerImage(bannerImage: String?): MediaItem {
    return this.copy(bannerImageUrl = bannerImage)
}

fun MediaItem.coverImageUrl(coverImageUrl: String?): MediaItem {
    return this.copy(coverImageUrl = coverImageUrl)
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