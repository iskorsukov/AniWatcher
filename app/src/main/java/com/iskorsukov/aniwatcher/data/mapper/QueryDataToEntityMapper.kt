package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity

class QueryDataToEntityMapper {

    fun mapMediaWithSchedulesList(data: SeasonAiringDataQuery.Data): List<MediaItemWithAiringSchedulesEntity> {
        if (data.Page?.media == null || data.Page.media.any { it?.airingSchedule?.nodes == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.media.filterNotNull().map {
            MediaItemWithAiringSchedulesEntity(
                MediaItemEntity.fromData(it),
                it.airingSchedule!!.nodes!!.filterNotNull().map {
                    AiringScheduleEntity.fromData(it)
                }
            )
        }
    }
}