package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
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
            airingSchedule = baseSeasonAiringDataSchedule(),
            season = MediaSeason.FALL
        )
    }

    fun baseSeasonAiringDataNode(): SeasonAiringDataQuery.Node {
        return SeasonAiringDataQuery.Node(
            1,
            1667833200,
            1,
            1
        )
    }

    fun baseSeasonAiringDataSchedule(): SeasonAiringDataQuery.AiringSchedule {
        return SeasonAiringDataQuery.AiringSchedule(
            nodes = listOf(
                baseSeasonAiringDataNode(),
                baseSeasonAiringDataNode().id(2).episode(2).airingAt(1667385638),
                baseSeasonAiringDataNode().id(3).episode(3).airingAt(1667644838),
                baseSeasonAiringDataNode().id(4).episode(4).airingAt(1667029460)
            )
        )
    }
}

fun SeasonAiringDataQuery.Node.id(id: Int): SeasonAiringDataQuery.Node {
    return this.copy(id = id)
}

fun SeasonAiringDataQuery.Node.episode(episode: Int): SeasonAiringDataQuery.Node {
    return this.copy(episode = episode)
}

fun SeasonAiringDataQuery.Node.airingAt(airingAt: Int): SeasonAiringDataQuery.Node {
    return this.copy(airingAt = airingAt)
}

fun SeasonAiringDataQuery.Medium.nullGenres(): SeasonAiringDataQuery.Medium {
    return this.copy(genres = null)
}

fun SeasonAiringDataQuery.Medium.emptyGenres(): SeasonAiringDataQuery.Medium {
    return this.copy(genres = emptyList())
}

fun SeasonAiringDataQuery.Medium.nullAiringSchedule(): SeasonAiringDataQuery.Medium {
    return this.copy(airingSchedule = null)
}