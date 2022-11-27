package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaFormat
import com.iskorsukov.aniwatcher.type.MediaRankType
import com.iskorsukov.aniwatcher.type.MediaSeason

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
            rankings = listOf(
                SeasonAiringDataQuery.Ranking(
                    type = MediaRankType.RATED,
                    rank = 3,
                    season = MediaSeason.FALL
                ),
                SeasonAiringDataQuery.Ranking(
                    type = MediaRankType.POPULAR,
                    rank = 2,
                    season = null
                ),
                SeasonAiringDataQuery.Ranking(
                    type = MediaRankType.RATED,
                    rank = 31,
                    season = null
                ),
                SeasonAiringDataQuery.Ranking(
                    type = MediaRankType.POPULAR,
                    rank = 1,
                    season = MediaSeason.FALL
                )
            ),
            meanScore = 1,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            airingSchedule = SeasonAiringDataQuery.AiringSchedule(
                airingScheduleNode = listOf(
                    baseSeasonAiringDataNode(),
                    baseSeasonAiringDataNode().id(2).episode(2).airingAt(1667385638),
                    baseSeasonAiringDataNode().id(3).episode(3).airingAt(1667644838),
                    baseSeasonAiringDataNode().id(4).episode(4).airingAt(1667029460)
                )
            ),
            season = MediaSeason.FALL,
            nextAiringEpisode = SeasonAiringDataQuery.NextAiringEpisode(1667833200),
            format = MediaFormat.TV
        )
    }

    fun baseSeasonAiringDataNode(): SeasonAiringDataQuery.AiringScheduleNode {
        return SeasonAiringDataQuery.AiringScheduleNode(
            1,
            1667833200,
            1,
            1
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
            popularity = 10,
            meanScore = 1,
            genres = listOf("Action", "Comedy"),
            siteUrl = "AniListUrl",
            airingSchedule = RangeAiringDataQuery.AiringSchedule1(
                airingScheduleNode = listOf(
                    baseRangeAiringDataNode(),
                    baseRangeAiringDataNode().id(2).episode(2).airingAt(1667385638),
                    baseRangeAiringDataNode().id(3).episode(3).airingAt(1667644838),
                    baseRangeAiringDataNode().id(4).episode(4).airingAt(1667029460)
                )
            ),
            nextAiringEpisode = RangeAiringDataQuery.NextAiringEpisode(1667833200),
            format = MediaFormat.TV
        )
    }

    fun baseRangeAiringDataNode(): RangeAiringDataQuery.AiringScheduleNode {
        return RangeAiringDataQuery.AiringScheduleNode(
            1,
            1667833200,
            1,
            1
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