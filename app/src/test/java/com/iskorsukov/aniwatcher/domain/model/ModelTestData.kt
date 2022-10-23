package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.WeekAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaRankType
import com.iskorsukov.aniwatcher.type.MediaSeason

object ModelTestData {
    val MEDIA_ITEM = WeekAiringDataQuery.Media(
        1,
        WeekAiringDataQuery.Title(
            "TitleRomaji",
            "TitleEnglish",
            "TitleNative"
        ),
        "Description",
        WeekAiringDataQuery.CoverImage(
            "CoverImageMedium",
            "CoverImageColor"
        ),
        listOf(
            WeekAiringDataQuery.Ranking(
                MediaRankType.POPULAR,
                1,
                MediaSeason.FALL
            ),
            WeekAiringDataQuery.Ranking(
                MediaRankType.RATED,
                12,
                null
            )
        ),
        65,
        listOf("Action", "Comedy"),
        "AniListUrl"
    )

    val MEDIA_ITEM_FILTER_RANKING = MEDIA_ITEM.copy(
        rankings = listOf(
            WeekAiringDataQuery.Ranking(
                MediaRankType.POPULAR,
                1,
                null
            ),
            WeekAiringDataQuery.Ranking(
                MediaRankType.RATED,
                12,
                MediaSeason.FALL
            )
        )
    )

    val AIRING_SCHEDULE = WeekAiringDataQuery.AiringSchedule(
        1,
        1000,
        12,
        MEDIA_ITEM
    )
}