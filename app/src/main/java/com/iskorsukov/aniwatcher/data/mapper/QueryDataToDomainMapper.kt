package com.iskorsukov.aniwatcher.data.mapper

import com.iskorsukov.aniwatcher.WeekAiringDataQuery
import com.iskorsukov.aniwatcher.domain.exception.MissingDataException
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem

class QueryDataToDomainMapper {

    fun map(data: WeekAiringDataQuery.Data): List<AiringScheduleItem> {
        return data.Page?.airingSchedules?.filterNotNull()?.filter {
            it.media != null
        }?.map {
            AiringScheduleItem.fromData(it)
        } ?: throw MissingDataException()
    }
}