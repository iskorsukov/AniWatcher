package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import javax.inject.Inject

class QueryDataToEntityMapper @Inject constructor() {

    /**
     * Maps seasonal query data to db entities
     *
     * @param data seasonal query data
     */
    fun mapMediaWithSchedulesList(data: SeasonAiringDataQuery.Data): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        // check for invalid null values
        if (data.Page?.media == null || data.Page.media.any { it?.airingSchedule?.airingScheduleNode == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.media
            .filterNotNull()
            .associate { queryMedia ->
                Pair(
                    MediaItemEntity.fromData(queryMedia),
                    queryMedia.airingSchedule!!.airingScheduleNode!!.filterNotNull().map { node -> // non-nullability enforced by earlier check
                        AiringScheduleEntity.fromData(node)
                    }
                )
            }
            .let {
                reassignPopularityToMedia(it)
            }
    }

    /**
     * Maps weekly query data to db entities
     *
     * @param data weekly query data
     */
    fun mapMediaWithSchedulesList(data: RangeAiringDataQuery.Data): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        // check for invalid null values
        if (data.Page?.airingSchedules == null || data.Page.airingSchedules.any { it?.media?.airingSchedule?.airingScheduleNode == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.airingSchedules
            .filterNotNull()
            .filter { it.media?.isAdult != null && it.media.isAdult == false } // filter adult media
            .associate { outerSchedule ->
                val queryMedia = outerSchedule.media!! // non-nullability enforced by earlier check
                Pair(
                    MediaItemEntity.fromData(queryMedia),
                    queryMedia.airingSchedule!!.airingScheduleNode!!.filterNotNull().map { node -> // non-nullability enforced by earlier check
                        AiringScheduleEntity.fromData(node)
                    }
                )
            }
            .let {
                reassignPopularityToMedia(it)
            }
    }


    /**
     * Reassigns base popularity value (number of interactions with media) to index in sorted by popularity list
     *
     * @param map media with following entity to airing schedules map
     * @return updated map
     */
    private fun reassignPopularityToMedia(map: Map<MediaItemEntity, List<AiringScheduleEntity>>): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        // sort map by popularity value descending
        val sortedMap = map.toSortedMap { first, second ->
            val firstRank = first.popularity ?: 0
            val secondRank = second.popularity ?: 0
            val diff = secondRank - firstRank
            if (diff == 0) {
                -1
            } else {
                diff
            }
        }
        // reassign popularity value to index from sorted map
        val updatedMap = mutableMapOf<MediaItemEntity, List<AiringScheduleEntity>>()
        sortedMap.onEachIndexed { index, entry ->
            val updatedKey = entry.key.copy(
                popularity = index + 1
            )
            updatedMap[updatedKey] = entry.value
        }
        return updatedMap
    }
}