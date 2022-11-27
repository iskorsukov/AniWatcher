package com.iskorsukov.aniwatcher.domain.notification

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NotificationsRepository {

    val notificationsFlow: Flow<List<NotificationItem>>

    val unreadNotificationsCounterStateFlow: StateFlow<Int>

    suspend fun getPendingSchedulesToNotify(): List<AiringScheduleItem>

    suspend fun saveNotification(notificationItem: NotificationItem)

    suspend fun increaseUnreadNotificationsCounter()

    suspend fun resetUnreadNotificationsCounter()
}