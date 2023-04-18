package com.iskorsukov.aniwatcher.ui.notification

import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import java.util.concurrent.TimeUnit

data class NotificationsUiState(
    val notifications: List<NotificationItem>,
    val timeInMinutes: Long
): UiState {
    companion object {
        val DEFAULT = NotificationsUiState(
            notifications = emptyList(),
            timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())
        )
    }
}