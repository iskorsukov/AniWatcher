package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.MainActivityInputEvent
import javax.inject.Inject

sealed interface NotificationsPermissionEvent: MainActivityInputEvent
object NotificationsPermissionMissing : NotificationsPermissionEvent
object NotificationsPermissionGrantClicked : NotificationsPermissionEvent
object NotificationsPermissionDisableClicked : NotificationsPermissionEvent
object NotificationsPermissionGranted : NotificationsPermissionEvent
object NotificationsPermissionDenied : NotificationsPermissionEvent

class NotificationsPermissionEventHandler @Inject constructor() {

    fun handleEvent(
        inputEvent: NotificationsPermissionEvent,
        originalUiState: MainActivityUiState,
        settingsRepository: SettingsRepository
    ): MainActivityUiState {
        return when (inputEvent) {
            is NotificationsPermissionMissing -> {
                settingsRepository.setNotificationsEnabled(false)
                return originalUiState.copy(
                    showNotificationsPermissionRationale = true
                )
            }
            is NotificationsPermissionGrantClicked -> {
                originalUiState.copy(
                    showNotificationsPermissionRationale = false,
                    launchNotificationPermissionRequest = true
                )
            }
            is NotificationsPermissionDisableClicked -> {
                settingsRepository.setNotificationsEnabled(false)
                originalUiState.copy(
                    showNotificationsPermissionRationale = false
                )
            }
            is NotificationsPermissionGranted -> {
                settingsRepository.setNotificationsEnabled(true)
                originalUiState.copy(
                    notificationsPermissionGranted = true,
                    launchNotificationPermissionRequest = false
                )
            }
            is NotificationsPermissionDenied -> {
                settingsRepository.setNotificationsEnabled(false)
                originalUiState.copy(
                    notificationsPermissionGranted = false,
                    launchNotificationPermissionRequest = false
                )
            }
        }
    }
}