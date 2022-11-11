package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity
import javax.inject.Inject

class QueryDataToEntityMapper @Inject constructor() {

    fun mapMediaWithSchedulesList(data: SeasonAiringDataQuery.Data): List<MediaItemWithAiringSchedulesEntity> {
        if (data.Page?.media == null || data.Page.media.any { it?.airingSchedule?.airingScheduleNode == null }) {
            throw IllegalArgumentException("Unexpected null value in query data")
        }
        return data.Page.media.filterNotNull().map {
            MediaItemWithAiringSchedulesEntity(
                MediaItemEntity.fromData(it),
                it.airingSchedule!!.airingScheduleNode!!.filterNotNull().map {
                    AiringScheduleEntity.fromData(it)
                }
            )
        }
    }
}