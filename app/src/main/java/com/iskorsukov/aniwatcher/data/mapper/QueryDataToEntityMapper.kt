package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.type.MediaFormat
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
            .filter { it.media?.format != MediaFormat.ONA }
            .associate { outerSchedule ->
                val queryMedia = outerSchedule.media!! // non-nullability enforced by earlier check
                Pair(
                    MediaItemEntity.fromData(queryMedia),
                    queryMedia.airingSchedule!!.airingScheduleNode!!.filterNotNull().map { node -> // non-nullability enforced by earlier check
                        AiringScheduleEntity.fromData(node)
                    }
                )
            }
    }
}