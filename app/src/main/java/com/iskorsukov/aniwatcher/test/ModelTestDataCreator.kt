package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem

object ModelTestDataCreator {

    const val TIME_IN_MINUTES = 27749715L

    val baseMediaItem: MediaItem = MediaItem(
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
        popularity = 1,
        meanScore = 1,
        genres = listOf("Action", "Comedy"),
        siteUrl = "AniListUrl",
        status = MediaItem.LocalStatus.RELEASING,
        format = MediaItem.LocalFormat.TV,
        isFollowing = false,
        season = "FALL",
        year = 2022
    )

    fun baseAiringScheduleItem(): AiringScheduleItem {
        return AiringScheduleItem(
            1,
            "7.10.2022/18:00".toSeconds(),
            1
        )
    }

    fun baseAiringScheduleItemList(): List<AiringScheduleItem> {
        val base = baseAiringScheduleItem()
        return listOf(
            base,
            base.id(2).episode(2).airingAt("8.10.2022/13:40".toSeconds()),
            base.id(3).episode(3).airingAt("12.10.2022/18:00".toSeconds()),
            base.id(4).episode(4).airingAt("15.10.2022/18:00".toSeconds())
        )
    }

    fun baseAiringScheduleToMediaPairList(): List<Pair<AiringScheduleItem, MediaItem>> {
        val base = baseAiringScheduleItem()
        return listOf(
            base to baseMediaItem,
            base.id(2).episode(2).airingAt("8.10.2022/13:40".toSeconds()) to baseMediaItem,
            base.id(3).episode(3).airingAt("12.10.2022/18:00".toSeconds()) to baseMediaItem,
            base.id(4).episode(4).airingAt("15.10.2022/18:00".toSeconds()) to baseMediaItem
        )
    }

    fun baseNotificationItem(): NotificationItem {
        return NotificationItem(
            1,
            "7.10.2022/18:00".toSeconds() * 1000L,
            baseAiringScheduleItem(),
            baseMediaItem.isFollowing(true)
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

fun MediaItem.description(description: String): MediaItem {
    return this.copy(description = description)
}

fun MediaItem.popularity(popularity: Int?): MediaItem {
    return this.copy(popularity = popularity)
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