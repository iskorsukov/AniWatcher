package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.room.PersistentMediaDatabase
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersistentMediaDatabaseExecutor @Inject constructor(
    private val persistentMediaDatabase: PersistentMediaDatabase,
    private val clock: LocalClockSystem
) {

    private val persistentMediaDao = persistentMediaDatabase.persistentMediaDao()
    private val notificationsDao = persistentMediaDatabase.notificationsDao()

    val notificationsFlow: Flow<Map<MediaItemEntity, List<AiringScheduleAndNotificationEntity>>> =
        notificationsDao.getAll()

    val followedMediaFlow: Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>> = persistentMediaDao.getAll()

    fun getFollowedMediaWithAiringSchedules(mediaItemId: Int): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>> {
        return persistentMediaDao.getById(mediaItemId)
    }

    suspend fun getPendingNotifications(): Map<MediaItemEntity, List<AiringScheduleEntity>> {
        return notificationsDao.getPending(clock.currentTimeSeconds())
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

    suspend fun saveMediaWithSchedules(mediaToAiringSchedules: Pair<MediaItemEntity, List<AiringScheduleEntity>>) {
        withContext(DispatcherProvider.io()) {
            persistentMediaDatabase.withTransaction {
                persistentMediaDao.insertMedia(mediaToAiringSchedules.first)
                persistentMediaDao.insertSchedules(mediaToAiringSchedules.second)
            }
        }
    }

    suspend fun deleteMedia(mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            persistentMediaDao.deleteMedia(mediaItemId)
        }
    }
}