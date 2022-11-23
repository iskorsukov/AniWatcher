package com.iskorsukov.aniwatcher.domain.notification

import android.content.SharedPreferences
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val mediaDatabaseExecutor: MediaDatabaseExecutor,
    private val sharedPreferences: SharedPreferences
) : NotificationsRepository {

    override val notificationsFlow: Flow<List<NotificationItem>> =
        mediaDatabaseExecutor.notificationsFlow.map {
            it.map { entry ->
                entry.value.map {
                    NotificationItem.fromEntity(entry.key, it)
                }
            }.flatten().sortedByDescending { it.firedAtMillis }
        }

    private val _unreadNotificationsCounterStateFlow = MutableStateFlow(
        sharedPreferences.getInt(UNREAD_NOTIFICATIONS_KEY, 0)
    )
    override val unreadNotificationsCounterStateFlow: StateFlow<Int> =
        _unreadNotificationsCounterStateFlow

    override suspend fun saveNotification(notificationItem: NotificationItem) {
        mediaDatabaseExecutor.saveNotification(notificationItem)
    }

    override suspend fun increaseUnreadNotificationsCounter() {
        val currentValue = sharedPreferences.getInt(UNREAD_NOTIFICATIONS_KEY, 0)
        sharedPreferences.edit()
            .putInt(UNREAD_NOTIFICATIONS_KEY, currentValue + 1)
            .apply()
        _unreadNotificationsCounterStateFlow.value = currentValue + 1
    }

    override suspend fun resetUnreadNotificationsCounter() {
        sharedPreferences.edit()
            .putInt(UNREAD_NOTIFICATIONS_KEY, 0)
            .apply()
        _unreadNotificationsCounterStateFlow.value = 0
    }

    companion object {
        const val UNREAD_NOTIFICATIONS_KEY = "unread_notifications"
    }
}