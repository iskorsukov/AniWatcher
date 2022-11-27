package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AiringRepositoryImpl @Inject constructor(
    private val aniListQueryExecutor: AniListQueryExecutor,
    private val mapper: QueryDataToEntityMapper,
    private val mediaDatabaseExecutor: MediaDatabaseExecutor
) : AiringRepository {

    override val mediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> =
        mediaDatabaseExecutor.mediaDataFlow
            .map { mediaMap ->
                mediaMap.map {
                    val mediaItem = MediaItem.fromEntity(it.key.mediaItemEntity, it.key.followingEntity)
                    val airingSchedules = it.value.map {
                        AiringScheduleItem.fromEntity(it, mediaItem)
                    }
                    mediaItem to airingSchedules
                }.associate { it.first to it.second }
            }

    override fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>?> {
        return mediaDatabaseExecutor.getMediaWithAiringSchedulesAndFollowing(mediaItemId).map {
            val entry = it.entries.firstOrNull()
            if (entry == null) {
                null
            } else {
                val mediaItem = MediaItem.fromEntity(entry.key.mediaItemEntity, entry.key.followingEntity)
                val airingSchedules = entry.value.map {
                    AiringScheduleItem.fromEntity(it, mediaItem)
                }
                mediaItem to airingSchedules
            }
        }
    }

    override suspend fun loadSeasonAiringData(year: Int, season: String) {
        val entities = mutableMapOf<MediaItemEntity, List<AiringScheduleEntity>>()
        var data: SeasonAiringDataQuery.Data
        var page = 1
        do {
            data = aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            entities.putAll(mapper.mapMediaWithSchedulesList(data))
            page++
        } while (data.Page?.pageInfo?.hasNextPage == true)
        mediaDatabaseExecutor.updateMedia(entities)
    }

    override suspend fun loadRangeAiringData(startSeconds: Int, endSeconds: Int) {
        val entities = mutableMapOf<MediaItemEntity, List<AiringScheduleEntity>>()
        var data: RangeAiringDataQuery.Data
        var page = 1
        do {
            data = aniListQueryExecutor.rangeAiringDataQuery(startSeconds, endSeconds, page)
            entities.putAll(mapper.mapMediaWithSchedulesList(data))
            page++
        } while (data.Page?.pageInfo?.hasNextPage == true)
        mediaDatabaseExecutor.updateMedia(entities)
    }

    override suspend fun followMedia(mediaItem: MediaItem) {
        mediaDatabaseExecutor.followMedia(mediaItem.id)
    }

    override suspend fun unfollowMedia(mediaItem: MediaItem) {
        mediaDatabaseExecutor.unfollowMedia(mediaItem.id)
    }

    override suspend fun unfollowMedia(mediaItemList: List<MediaItem>) {
        mediaDatabaseExecutor.unfollowMedia(mediaItemList.map { it.id })
    }
}