package com.iskorsukov.aniwatcher.domain.airing

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.executor.PersistentMediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import com.iskorsukov.aniwatcher.domain.exception.ApolloException
import com.iskorsukov.aniwatcher.domain.exception.RoomException
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AiringRepositoryImpl @Inject constructor(
    private val aniListQueryExecutor: AniListQueryExecutor,
    private val mapper: QueryDataToEntityMapper,
    private val mediaDatabaseExecutor: MediaDatabaseExecutor,
    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor
) : AiringRepository {

    override val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = System.currentTimeMillis()
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.stateIn(
        CoroutineScope(Dispatchers.Main),
        SharingStarted.Lazily,
        TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())
    )

    override val mediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> =
        mediaDatabaseExecutor.mediaDataFlow
            .combine(persistentMediaDatabaseExecutor.followedMediaFlow) { mediaMap, followedEntityMap ->
                mediaMap
                    .mapNotNull { entry ->
                        mapToItems(entry.key, entry.value, followedEntityMap.containsKey(entry.key))
                    }
                    .associate { it.first to it.second }
            }

    override val followedMediaFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> =
        persistentMediaDatabaseExecutor.followedMediaFlow
            .map { mediaMap ->
                mediaMap
                    .mapNotNull { entry ->
                        mapToItems(entry.key, entry.value, true)
                    }
                    .associate { it.first to it.second }
            }

    override fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>?> {
        return mediaDatabaseExecutor.getMediaWithAiringSchedules(mediaItemId)
            .combine(persistentMediaDatabaseExecutor.followedMediaFlow) { mediaToAiringSchedules, followedEntityMap ->
                val persistedMedia: MediaItemEntity? = followedEntityMap.keys.find { entity -> entity.mediaId == mediaItemId }

                val mediaItemEntity: MediaItemEntity
                val scheduleEntitiesList: List<AiringScheduleEntity>
                if (mediaToAiringSchedules != null) {
                    mediaItemEntity = mediaToAiringSchedules.first
                    scheduleEntitiesList = mediaToAiringSchedules.second
                } else if (persistedMedia != null) {
                    mediaItemEntity = persistedMedia
                    scheduleEntitiesList = followedEntityMap.getOrDefault(persistedMedia, emptyList())
                } else {
                    return@combine null
                }

                mapToItems(mediaItemEntity, scheduleEntitiesList, persistedMedia != null)
            }
    }

    private fun mapToItems(
        mediaItemEntity: MediaItemEntity,
        airingScheduleEntityList: List<AiringScheduleEntity>,
        isFollowing: Boolean
    ): Pair<MediaItem, List<AiringScheduleItem>>? {
        return try {
            val mediaItem = MediaItem.fromEntity(
                mediaItemEntity,
                isFollowing
            )
            val airingSchedules = airingScheduleEntityList.map { schedule ->
                AiringScheduleItem.fromEntity(schedule)
            }
            mediaItem to airingSchedules
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
            return null
        }
    }

    override suspend fun loadSeasonAiringData(year: Int, season: String) {
        val entities = mutableMapOf<MediaItemEntity, List<AiringScheduleEntity>>()
        var data: SeasonAiringDataQuery.Data
        var page = 1
        do {
            data = try {
                aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            } catch (e: Exception) {
                throw ApolloException(e)
            }
            entities.putAll(mapper.mapMediaWithSchedulesList(data))
            page++
            yield()
            if (!coroutineContext.isActive) return
        } while (data.Page?.pageInfo?.hasNextPage == true)
        try {
            mediaDatabaseExecutor.updateMedia(entities)
            persistentMediaDatabaseExecutor.updateMedia(entities.keys.toList())
        } catch (e: Exception) {
            throw RoomException(e)
        }
    }

    override suspend fun loadRangeAiringData(startSeconds: Int, endSeconds: Int) {
        val entities = mutableMapOf<MediaItemEntity, List<AiringScheduleEntity>>()
        var data: RangeAiringDataQuery.Data
        var page = 1
        do {
            data = try {
                aniListQueryExecutor.rangeAiringDataQuery(startSeconds, endSeconds, page)
            } catch (e: Exception) {
                throw ApolloException(e)
            }
            entities.putAll(mapper.mapMediaWithSchedulesList(data))
            page++
            yield()
            if (!coroutineContext.isActive) return
        } while (data.Page?.pageInfo?.hasNextPage == true)
        try {
            mediaDatabaseExecutor.updateMedia(entities)
            persistentMediaDatabaseExecutor.updateMedia(entities.keys.toList())
        } catch (e: Exception) {
            throw RoomException(e)
        }
    }

    override suspend fun followMedia(mediaItem: MediaItem) {
        try {
            val mediaItemWithSchedules =
                mediaDatabaseExecutor.getMediaWithAiringSchedules(mediaItem.id).firstOrNull()
            if (mediaItemWithSchedules != null) {
                val mediaItemWithUpcomingSchedules = mediaItemWithSchedules.copy(
                    mediaItemWithSchedules.first,
                    mediaItemWithSchedules.second.filter { scheduleEntity ->
                        scheduleEntity.airingAt > TimeUnit.MINUTES.toSeconds(timeInMinutesFlow.value)
                    }
                )
                persistentMediaDatabaseExecutor.saveMediaWithSchedules(mediaItemWithUpcomingSchedules)
            }
        } catch (e: Exception) {
            throw RoomException(e)
        }
    }

    override suspend fun unfollowMedia(mediaItem: MediaItem) {
        try {
            persistentMediaDatabaseExecutor.deleteMedia(mediaItem.id)
        } catch (e: Exception) {
            throw RoomException(e)
        }
    }
}