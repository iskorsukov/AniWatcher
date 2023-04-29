package com.iskorsukov.aniwatcher.test

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.type.MediaFormat
import com.iskorsukov.aniwatcher.type.MediaSeason
import com.iskorsukov.aniwatcher.type.MediaStatus

object QueryTestDataCreator {

    fun seasonAiringDataMedium(
        id: Int,
        title: SeasonAiringDataQuery.Title? = null,
        description: String? = null,
        coverImage: SeasonAiringDataQuery.CoverImage? = null,
        bannerImage: String? = null,
        popularity: Int? = null,
        format: MediaFormat? = null,
        season: MediaSeason? = null,
        seasonYear: Int? = null,
        meanScore: Int? = null,
        genres: List<String>? = null,
        studios: SeasonAiringDataQuery.Studios? = null,
        siteUrl: String? = null,
        status: MediaStatus? = null,
        airingSchedule: SeasonAiringDataQuery.AiringSchedule? = null
    ): SeasonAiringDataQuery.Medium {
        return SeasonAiringDataQuery.Medium(
            id = id,
            title = title,
            description = description,
            coverImage = coverImage,
            bannerImage = bannerImage,
            popularity = popularity,
            format = format,
            season = season,
            seasonYear = seasonYear,
            meanScore = meanScore,
            genres = genres,
            studios = studios,
            siteUrl = siteUrl,
            status = status,
            airingSchedule = airingSchedule
        )
    }

    fun seasonAiringDataTitle(
        romaji: String? = null,
        english: String? = null,
        native: String? = null
    ): SeasonAiringDataQuery.Title {
        return SeasonAiringDataQuery.Title(
            romaji = romaji,
            english = english,
            native = native
        )
    }

    fun seasonAiringDataCoverImage(
        large: String? = null,
        color: String? = null
    ): SeasonAiringDataQuery.CoverImage {
        return SeasonAiringDataQuery.CoverImage(
            large = large,
            color = color
        )
    }

    fun seasonAiringDataStudios(
        studios: List<String>?
    ): SeasonAiringDataQuery.Studios {
        return SeasonAiringDataQuery.Studios(
            studioNode = studios?.map { studioName ->
                SeasonAiringDataQuery.StudioNode(
                    name = studioName
                )
            }
        )
    }

    fun seasonAiringDataSchedule(
        airingScheduleNodes: List<SeasonAiringDataQuery.AiringScheduleNode>? = null
    ): SeasonAiringDataQuery.AiringSchedule {
        return SeasonAiringDataQuery.AiringSchedule(
            airingScheduleNode = airingScheduleNodes
        )
    }

    fun seasonAiringDataScheduleNode(
        id: Int,
        airingAt: Int = 1,
        episode: Int = 1,
        mediaId: Int
    ): SeasonAiringDataQuery.AiringScheduleNode {
        return SeasonAiringDataQuery.AiringScheduleNode(
            id = id,
            airingAt = airingAt,
            episode = episode,
            mediaId = mediaId
        )
    }

    fun rangeAiringDataMedia(
        id: Int,
        title: RangeAiringDataQuery.Title? = null,
        description: String? = null,
        coverImage: RangeAiringDataQuery.CoverImage? = null,
        bannerImage: String? = null,
        popularity: Int? = null,
        format: MediaFormat? = null,
        season: MediaSeason? = null,
        seasonYear: Int? = null,
        meanScore: Int? = null,
        genres: List<String>? = null,
        studios: RangeAiringDataQuery.Studios? = null,
        siteUrl: String? = null,
        status: MediaStatus? = null,
        isAdult: Boolean? = false,
        airingSchedule: RangeAiringDataQuery.AiringSchedule1? = null
    ): RangeAiringDataQuery.Media {
        return RangeAiringDataQuery.Media(
            id = id,
            title = title,
            description = description,
            coverImage = coverImage,
            bannerImage = bannerImage,
            popularity = popularity,
            format = format,
            season = season,
            seasonYear = seasonYear,
            meanScore = meanScore,
            genres = genres,
            studios = studios,
            siteUrl = siteUrl,
            status = status,
            isAdult = isAdult,
            airingSchedule = airingSchedule
        )
    }

    fun rangeAiringDataTitle(
        romaji: String? = null,
        english: String? = null,
        native: String? = null
    ): RangeAiringDataQuery.Title {
        return RangeAiringDataQuery.Title(
            romaji = romaji,
            english = english,
            native = native
        )
    }

    fun rangeAiringDataCoverImage(
        large: String? = null,
        color: String? = null
    ): RangeAiringDataQuery.CoverImage {
        return RangeAiringDataQuery.CoverImage(
            large = large,
            color = color
        )
    }

    fun rangeAiringDataStudios(
        studios: List<String>?
    ): RangeAiringDataQuery.Studios {
        return RangeAiringDataQuery.Studios(
            studioNode = studios?.map { studioName ->
                RangeAiringDataQuery.StudioNode(
                    name = studioName
                )
            }
        )
    }

    fun rangeAiringDataSchedule(
        airingScheduleNodes: List<RangeAiringDataQuery.AiringScheduleNode>? = null
    ): RangeAiringDataQuery.AiringSchedule1 {
        return RangeAiringDataQuery.AiringSchedule1(
            airingScheduleNode = airingScheduleNodes
        )
    }

    fun rangeAiringDataScheduleNode(
        id: Int,
        airingAt: Int = 1,
        episode: Int = 1,
        mediaId: Int
    ): RangeAiringDataQuery.AiringScheduleNode {
        return RangeAiringDataQuery.AiringScheduleNode(
            id = id,
            airingAt = airingAt,
            episode = episode,
            mediaId = mediaId
        )
    }
}