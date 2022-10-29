package com.iskorsukov.aniwatcher

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
                medium = "CoverImageMedium",
                color = "CoverImageColor"
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
            1,
            1,
            1
        )
    }

    fun baseSeasonAiringDataSchedule(): SeasonAiringDataQuery.AiringSchedule {
        return SeasonAiringDataQuery.AiringSchedule(
            nodes = listOf(
                baseSeasonAiringDataNode(),
                baseSeasonAiringDataNode().id(2).episode(2),
                baseSeasonAiringDataNode().id(3).episode(3),
                baseSeasonAiringDataNode().id(4).episode(4)
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

fun SeasonAiringDataQuery.Medium.nullGenres(): SeasonAiringDataQuery.Medium {
    return this.copy(genres = null)
}

fun SeasonAiringDataQuery.Medium.emptyGenres(): SeasonAiringDataQuery.Medium {
    return this.copy(genres = emptyList())
}

fun SeasonAiringDataQuery.Medium.nullAiringSchedule(): SeasonAiringDataQuery.Medium {
    return this.copy(airingSchedule = null)
}