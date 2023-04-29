package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem

object ModelTestDataCreator {

    const val TIME_IN_MINUTES = 27749715L

    fun mediaItem(
        id : Int,
        title: MediaItem.Title = mediaItemTitle(),
        description: String? = null,
        coverImageUrl: String? = null,
        colorStr: String? = null,
        bannerImageUrl: String? = null,
        mainStudio: String? = null,
        popularity: Int? = null,
        meanScore: Int? = null,
        genres: List<String> = emptyList(),
        siteUrl: String? = null,
        status: MediaItem.LocalStatus? = null,
        format: MediaItem.LocalFormat? = null,
        isFollowing: Boolean = false,
        season: String? = null,
        year: Int? = null
    ): MediaItem {
        return MediaItem(
            id = id,
            title = title,
            description = description,
            coverImageUrl = coverImageUrl,
            colorStr = colorStr,
            bannerImageUrl = bannerImageUrl,
            mainStudio = mainStudio,
            popularity = popularity,
            meanScore = meanScore,
            genres = genres,
            siteUrl = siteUrl,
            status = status,
            format = format,
            isFollowing = isFollowing,
            season = season,
            year = year
        )
    }

    fun mediaItemTitle(
        romaji: String? = null,
        english: String? = null,
        native: String? = null
    ): MediaItem.Title {
        return MediaItem.Title(
            romaji = romaji,
            english = english,
            native = native
        )
    }

    fun airingScheduleItem(
        id: Int,
        airingAt: Int = 1,
        episode: Int = 1
    ): AiringScheduleItem {
        return AiringScheduleItem(
            id = id,
            airingAt = airingAt,
            episode = episode
        )
    }

    fun notificationItem(
        id: Int?,
        firedAtMillis: Long = 1L,
        airingScheduleItem: AiringScheduleItem,
        mediaItem: MediaItem
    ): NotificationItem {
        return NotificationItem(
            id = id,
            firedAtMillis = firedAtMillis,
            airingScheduleItem = airingScheduleItem,
            mediaItem = mediaItem
        )
    }

    fun previewData(isFollowing: Boolean = false): Pair<MediaItem, List<AiringScheduleItem>> {
        val mediaItem = MediaItem(
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
            isFollowing = isFollowing,
            season = "FALL",
            year = 2022
        )
        val airingSchedules = listOf(
            airingScheduleItem(id = 1, airingAt = "8.10.2022/13:40".toSeconds(), episode = 1),
            airingScheduleItem(id = 2, airingAt = "11.10.2022/18:00".toSeconds(), episode = 2),
            airingScheduleItem(id = 3, airingAt = "14.10.2022/18:00".toSeconds(), episode = 3)
        )
        return mediaItem to airingSchedules
    }
}