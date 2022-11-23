package com.iskorsukov.aniwatcher.domain.notification

import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val mediaDatabaseExecutor: MediaDatabaseExecutor
) : NotificationsRepository {

    override val notificationsFlow: Flow<List<NotificationItem>> =
        mediaDatabaseExecutor.notificationsFlow.map {
            it.map { entry ->
                entry.value.map {
                    NotificationItem.fromEntity(entry.key, it)
                }
            }.flatten().sortedByDescending { it.firedAtMillis }
        }

    override suspend fun saveNotification(notificationItem: NotificationItem) {
        mediaDatabaseExecutor.saveNotification(notificationItem)
    }
}