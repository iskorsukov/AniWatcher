package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.domain.exception.MissingDataException
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem

class WeekAiringRepository(private val executor: AniListQueryExecutor) {

    suspend fun loadWeekAiringData(weekStart: Int, weekEnd: Int): List<AiringScheduleItem> {
        val data = executor.weekAiringDataQuery(weekStart, weekEnd)
        return data.Page?.airingSchedules?.filterNotNull()?.filter {
            it.media != null
        }?.map {
            AiringScheduleItem.fromData(it)
        } ?: throw MissingDataException()
    }
}