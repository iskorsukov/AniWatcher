package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import javax.inject.Inject

class QueryDataToEntityMapper @Inject constructor() {

    fun mapMediaWithSchedulesList(data: SeasonAiringDataQuery.Data): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        if (data.Page?.media == null || data.Page.media.any { it?.airingSchedule?.airingScheduleNode == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.media
            .filterNotNull()
            .associate {
            MediaItemEntity.fromData(it) to
                    it.airingSchedule!!.airingScheduleNode!!.filterNotNull().map {
                        AiringScheduleEntity.fromData(it)
                    }
        }
    }

    fun mapMediaWithSchedulesList(data: RangeAiringDataQuery.Data): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        if (data.Page?.airingSchedules == null ||
            data.Page.airingSchedules.any { it?.media?.airingSchedule == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.airingSchedules
            .filterNotNull()
            .filter { it.media?.format == null || MediaItem.LocalFormat.values().map { it.name }.contains(it.media.format.name) }
            .filter { it.media?.isAdult != null && it.media.isAdult == false }
            .associate { outerSchedule ->
            val media = outerSchedule.media!!
            MediaItemEntity.fromData(media) to
                    media.airingSchedule!!.airingScheduleNode!!.filterNotNull().map {
                        AiringScheduleEntity.fromData(it)
                    }
        }
    }
}