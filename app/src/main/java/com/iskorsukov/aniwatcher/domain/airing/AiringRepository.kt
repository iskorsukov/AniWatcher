package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper

class AiringRepository(
    private val aniListQueryExecutor: AniListQueryExecutor,
    private val mapper: QueryDataToEntityMapper,
    private val mediaDatabaseExecutor: MediaDatabaseExecutor) {

    suspend fun loadSeasonAiringData(year: Int, season: String, page: Int) {
        val data = aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        val entities = mapper.mapMediaWithSchedulesList(data)
        mediaDatabaseExecutor.updateMedia(entities)
    }
}