package com.iskorsukov.aniwatcher.domain.notification

import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    val notificationsFlow: Flow<List<NotificationItem>>

    suspend fun saveNotification(notificationItem: NotificationItem)
}