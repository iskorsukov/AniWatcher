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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AiringRepositoryImpl @Inject constructor(
    private val aniListQueryExecutor: AniListQueryExecutor,
    private val mapper: QueryDataToEntityMapper,
    private val mediaDatabaseExecutor: MediaDatabaseExecutor,
    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor
) : AiringRepository {

    override val mediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> =
        mediaDatabaseExecutor.mediaDataFlow
            .combine(persistentMediaDatabaseExecutor.followedMediaFlow) { mediaMap, followedEntityMap ->
                mediaMap
                    .mapNotNull { entry ->
                        val mediaItem = try {
                            MediaItem.fromEntity(
                                entry.key,
                                followedEntityMap.containsKey(entry.key)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            FirebaseCrashlytics.getInstance().recordException(e)
                            return@mapNotNull null
                        }
                        val airingSchedules = try {
                            entry.value.map { schedule ->
                                AiringScheduleItem.fromEntity(schedule)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            FirebaseCrashlytics.getInstance().recordException(e)
                            return@mapNotNull null
                        }
                        mediaItem to airingSchedules
                    }
                    .associate { it.first to it.second }
            }

    val followedMediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>> =
        persistentMediaDatabaseExecutor.followedMediaFlow
            .map { mediaMap ->
                mediaMap
                    .mapNotNull { entry ->
                        val mediaItem = try {
                            MediaItem.fromEntity(
                                entry.key,
                                true
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            FirebaseCrashlytics.getInstance().recordException(e)
                            return@mapNotNull null
                        }
                        val airingSchedules = try {
                            entry.value.map { schedule ->
                                AiringScheduleItem.fromEntity(schedule)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            FirebaseCrashlytics.getInstance().recordException(e)
                            return@mapNotNull null
                        }
                        mediaItem to airingSchedules
                    }
                    .associate { it.first to it.second }
            }

    override fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>?> {
        return mediaDatabaseExecutor.getMediaWithAiringSchedules(mediaItemId)
            .combine(persistentMediaDatabaseExecutor.followedMediaFlow) { mediaToAiringSchedules, followedEntityMap ->
                if (mediaToAiringSchedules == null)
                    return@combine null
                val mediaItemEntity = mediaToAiringSchedules.first
                val scheduleEntitiesList = mediaToAiringSchedules.second
                val mediaItem = try {
                    MediaItem.fromEntity(
                        mediaItemEntity,
                        followedEntityMap.containsKey(mediaItemEntity)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    return@combine null
                }
                val airingSchedules = try {
                    scheduleEntitiesList.map { schedule ->
                        AiringScheduleItem.fromEntity(schedule)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList()
                }
                mediaItem to airingSchedules
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
        } catch (e: Exception) {
            throw RoomException(e)
        }
    }

    override suspend fun followMedia(mediaItem: MediaItem) {
        try {
            val mediaItemWithSchedules =
                mediaDatabaseExecutor.getMediaWithAiringSchedules(mediaItem.id).firstOrNull()
            if (mediaItemWithSchedules != null) {
                persistentMediaDatabaseExecutor.saveMediaWithSchedules(mediaItemWithSchedules)
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