package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.combined.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MediaDatabaseExecutor @Inject constructor(
    private val mediaDatabase: MediaDatabase,
    private val clock: LocalClockSystem,
    settingsRepository: SettingsRepository
) {
    private val mediaDao = mediaDatabase.mediaDao()
    private val notificationsDao = mediaDatabase.notificationsDao()

    private val settingsStateFlow = settingsRepository.settingsStateFlow

    val mediaDataFlow: Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>> =
        mediaDao.getAllNotAired(clock.currentTimeSeconds())
            .map { map ->
                reassignPopularityToMedia(map)
            }
            .map { map ->
                filterExtraFollowedMedia(map)
            }

    val notificationsFlow: Flow<Map<MediaItemEntity, List<AiringScheduleAndNotificationEntity>>> =
        notificationsDao.getAll()

    suspend fun updateMedia(mediaToAiringSchedulesMap: Map<MediaItemEntity, List<AiringScheduleEntity>>) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                mediaDao.clearNotFollowedAiringSchedules()
                mediaDao.clearNotFollowedMedia()
                mediaDao.insertMedia(mediaToAiringSchedulesMap.keys.toList())
                mediaDao.insertSchedules(mediaToAiringSchedulesMap.values.flatten())
            }
        }
    }

    fun getMediaWithAiringSchedulesAndFollowing(mediaItemId: Int): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>> {
        return mediaDao.getByIdNotAired(mediaItemId, clock.currentTimeSeconds())
    }

    suspend fun getPendingNotifications(): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        return notificationsDao.getPending(clock.currentTimeSeconds())
    }

    suspend fun followMedia(mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                mediaDao.clearNotNotifiedAiredSchedules(mediaItemId, clock.currentTimeSeconds()) // so that only aired schedules from now on get notifications
                mediaDao.followMedia(FollowingEntity(null, mediaItemId))
            }
        }
    }

    suspend fun unfollowMedia(vararg mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                notificationsDao.clearNotificationsByMediaId(*mediaItemId)
                mediaDao.unfollowMedia(*mediaItemId)
            }
        }
    }

    suspend fun saveNotification(notificationItem: NotificationItem) {
        withContext(DispatcherProvider.io()) {
            notificationsDao.insertNotification(
                NotificationItemEntity(
                    null,
                    notificationItem.firedAtMillis,
                    notificationItem.airingScheduleItem.id
                )
            )
        }
    }

    /**
     * Reassigns base popularity value (number of interactions with media) to index in sorted by popularity list
     *
     * @param map media with following entity to airing schedules map
     * @return updated map
     */
    private fun reassignPopularityToMedia(map: Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>): Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>> {
        // sort map by popularity value descending
        val sortedMap = map.toSortedMap { first, second ->
            val firstRank = first.mediaItemEntity.popularity ?: 0
            val secondRank = second.mediaItemEntity.popularity ?: 0
            val diff = secondRank - firstRank
            if (diff == 0) {
                -1
            } else {
                diff
            }
        }
        // reassign popularity value to index from sorted map
        val updatedMap = mutableMapOf<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>()
        sortedMap.onEachIndexed { index, entry ->
            val updatedKey = entry.key.copy(
                mediaItemEntity = entry.key.mediaItemEntity.copy(popularity = index + 1)
            )
            updatedMap[updatedKey] = entry.value
        }
        return updatedMap
    }

    /**
     * Filters extra followed media received from db
     * By default followed media is not deleted from db. Because of that media from one season will
     * show up if user changes season (db gets wiped and repopulated by new media, but followed media stays).
     * Because of that we need to filter extra media based on schedule type and/or current season.
     *
     * @param map media with following entity to airing schedules map
     * @return filtered map
     */
    private fun filterExtraFollowedMedia(map: Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>): Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>> {
        val settingsState = settingsStateFlow.value
        val selectedSeasonYear = settingsState.selectedSeasonYear
        return map.filter { entry ->
            if (settingsState.scheduleType == ScheduleType.ALL) {
                // if there are any episodes airing in current week, media stays
                val startToEnd = DateTimeHelper.currentWeekStartToEndSeconds(Calendar.getInstance())
                entry.value.any { schedule ->
                    schedule.airingAt in startToEnd.first..startToEnd.second
                }
            } else {
                // if there are any episodes airing in current week, media stays
                val media = entry.key.mediaItemEntity
                media.season == selectedSeasonYear.season.name && media.year == selectedSeasonYear.year
            }
        }
    }
}