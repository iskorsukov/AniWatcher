package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaFormat
import com.iskorsukov.aniwatcher.type.MediaSeason
import com.iskorsukov.aniwatcher.type.MediaStatus

object QueryTestDataCreator {

    fun baseSeasonAiringDataMedium(): SeasonAiringDataQuery.Medium {
        return SeasonAiringDataQuery.Medium(
            id = 1,
            title = SeasonAiringDataQuery.Title(
                "TitleRomaji",
                "TitleEnglish",
                "TitleNative"
            ),
            description = "Description",
            coverImage = SeasonAiringDataQuery.CoverImage(
                large = "https://img1.goodfon.ru/original/1024x768/9/d6/kotionok-malysh-vzgliad-trava-boke.jpg",
                color = "#43aee4"
            ),
            bannerImage = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/139587-XFbotQOPcLC4.jpg",
            studios = SeasonAiringDataQuery.Studios(
                listOf(SeasonAiringDataQuery.StudioNode("Studio"))
            ),
            popularity = 1,
            meanScore = 1,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            airingSchedule = SeasonAiringDataQuery.AiringSchedule(
                baseSeasonAiringDataNodeList()
            ),
            season = MediaSeason.FALL,
            seasonYear = 2022,
            format = MediaFormat.TV,
            status = MediaStatus.RELEASING
        )
    }

    fun baseSeasonAiringDataNode(): SeasonAiringDataQuery.AiringScheduleNode {
        return SeasonAiringDataQuery.AiringScheduleNode(
            1,
            "7.10.2022/18:00".toSeconds(),
            1,
            1
        )
    }

    fun baseSeasonAiringDataNodeList(): List<SeasonAiringDataQuery.AiringScheduleNode> {
        val base = baseSeasonAiringDataNode()
        return listOf(
            base,
            base.id(2).episode(2).airingAt("8.10.2022/13:40".toSeconds()),
            base.id(3).episode(3).airingAt("12.10.2022/18:00".toSeconds()),
            base.id(4).episode(4).airingAt("15.10.2022/18:00".toSeconds())
        )
    }

    fun baseRangeAiringDataMedium(): RangeAiringDataQuery.Media {
        return RangeAiringDataQuery.Media(
            id = 1,
            title = RangeAiringDataQuery.Title(
                "TitleRomaji",
                "TitleEnglish",
                "TitleNative"
            ),
            description = "Description",
            coverImage = RangeAiringDataQuery.CoverImage(
                large = "https://img1.goodfon.ru/original/1024x768/9/d6/kotionok-malysh-vzgliad-trava-boke.jpg",
                color = "#43aee4"
            ),
            bannerImage = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/139587-XFbotQOPcLC4.jpg",
            studios = RangeAiringDataQuery.Studios(
                listOf(RangeAiringDataQuery.StudioNode("Studio"))
            ),
            popularity = 1,
            meanScore = 1,
            isAdult = false,
            season = MediaSeason.FALL,
            seasonYear = 2022,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            airingSchedule = RangeAiringDataQuery.AiringSchedule1(
                baseRangeAiringDataNodeList()
            ),
            format = MediaFormat.TV,
            status = MediaStatus.RELEASING
        )
    }

    fun baseRangeAiringDataNode(): RangeAiringDataQuery.AiringScheduleNode {
        return RangeAiringDataQuery.AiringScheduleNode(
            1,
            "7.10.2022/18:00".toSeconds(),
            1,
            1
        )
    }

    fun baseRangeAiringDataNodeList(): List<RangeAiringDataQuery.AiringScheduleNode> {
        val base = baseRangeAiringDataNode()
        return listOf(
            base,
            base.id(2).episode(2).airingAt("8.10.2022/13:40".toSeconds()),
            base.id(3).episode(3).airingAt("12.10.2022/18:00".toSeconds()),
            base.id(4).episode(4).airingAt("15.10.2022/18:00".toSeconds())
        )
    }
}

fun SeasonAiringDataQuery.AiringScheduleNode.id(id: Int): SeasonAiringDataQuery.AiringScheduleNode {
    return this.copy(id = id)
}

fun SeasonAiringDataQuery.AiringScheduleNode.episode(episode: Int): SeasonAiringDataQuery.AiringScheduleNode {
    return this.copy(episode = episode)
}

fun SeasonAiringDataQuery.AiringScheduleNode.airingAt(airingAt: Int): SeasonAiringDataQuery.AiringScheduleNode {
    return this.copy(airingAt = airingAt)
}

fun SeasonAiringDataQuery.Medium.nullAiringSchedule(): SeasonAiringDataQuery.Medium {
    return this.copy(airingSchedule = null)
}

fun RangeAiringDataQuery.AiringScheduleNode.id(id: Int): RangeAiringDataQuery.AiringScheduleNode {
    return this.copy(id = id)
}

fun RangeAiringDataQuery.AiringScheduleNode.episode(episode: Int): RangeAiringDataQuery.AiringScheduleNode {
    return this.copy(episode = episode)
}

fun RangeAiringDataQuery.AiringScheduleNode.airingAt(airingAt: Int): RangeAiringDataQuery.AiringScheduleNode {
    return this.copy(airingAt = airingAt)
}

fun RangeAiringDataQuery.Media.nullAiringSchedule(): RangeAiringDataQuery.Media {
    return this.copy(airingSchedule = null)
}