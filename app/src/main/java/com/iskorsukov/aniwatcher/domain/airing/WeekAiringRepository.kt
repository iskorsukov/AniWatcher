package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToDomainMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem

class WeekAiringRepository(
    private val executor: AniListQueryExecutor,
    private val mapper: QueryDataToDomainMapper) {

    suspend fun loadWeekAiringData(weekStart: Int, weekEnd: Int): List<AiringScheduleItem> {
        val data = executor.weekAiringDataQuery(weekStart, weekEnd)
        return mapper.map(data)
    }
}