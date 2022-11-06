package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.mediaItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AiringRepository @Inject constructor(
    private val aniListQueryExecutor: AniListQueryExecutor,
    private val mapper: QueryDataToEntityMapper,
    private val mediaDatabaseExecutor: MediaDatabaseExecutor) {

    val mediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> = mediaDatabaseExecutor.mediaDataFlow
            .map { entityList ->
                entityList.associate { entity ->
                    val airingSchedules = AiringScheduleItem.fromEntity(entity)
                    val mediaItem = if (airingSchedules.isNotEmpty()) {
                        airingSchedules.first().mediaItem
                    } else {
                        MediaItem.fromEntity(
                            entity.mediaItemWithAiringSchedulesEntity.mediaItemEntity,
                            entity.followingEntity
                        )
                    }
                    mediaItem to airingSchedules
                }
            }

    suspend fun loadSeasonAiringData(year: Int, season: String) {
        val entities = mutableListOf<MediaItemWithAiringSchedulesEntity>()
        var data: SeasonAiringDataQuery.Data
        var page = 1
        do {
            data = aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            entities.addAll(mapper.mapMediaWithSchedulesList(data))
            page++
        } while (data.Page?.pageInfo?.hasNextPage == true)
        mediaDatabaseExecutor.updateMedia(entities)
    }

    suspend fun followMedia(mediaItem: MediaItem) {
        mediaDatabaseExecutor.followMedia(mediaItem.id)
    }

    suspend fun unfollowMedia(mediaItem: MediaItem) {
        mediaDatabaseExecutor.unfollowMedia(mediaItem.id)
    }
}