package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.*
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.work.util.LocalClockSystem
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaDatabaseExecutor @Inject constructor(
    private val mediaDatabase: MediaDatabase,
    private val clock: LocalClockSystem
) {
    private val mediaDao = mediaDatabase.mediaDao()
    private val notificationsDao = mediaDatabase.notificationsDao()

    val mediaDataFlow: Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>> =
        mediaDao.getAllNotAired(clock.currentTimeSeconds())

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

    suspend fun unfollowMedia(mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                notificationsDao.clearNotificationsByMediaId(mediaItemId)
                mediaDao.unfollowMedia(mediaItemId)
            }
        }
    }

    suspend fun unfollowMedia(mediaItemIdList: List<Int>) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                notificationsDao.clearNotificationsByMediaId(mediaItemIdList)
                mediaDao.unfollowMedia(mediaItemIdList)
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
}