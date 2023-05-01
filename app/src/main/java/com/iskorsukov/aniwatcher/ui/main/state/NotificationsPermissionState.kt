package com.iskorsukov.aniwatcher.ui.main.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository

@Composable
fun rememberNotificationsPermissionState(
    settingsRepository: SettingsRepository,
    notificationsEnabled: Boolean,
    notificationsPermissionGranted: Boolean,
    shouldShowRationale: Boolean,
    permissionRequestResultGranted: Boolean?
): NotificationsPermissionState {
    return remember(
        settingsRepository,
        notificationsEnabled,
        notificationsPermissionGranted,
        permissionRequestResultGranted,
        shouldShowRationale
    ) {
        NotificationsPermissionState(
            notificationsPermissionGranted = notificationsPermissionGranted,
            permissionRequestResultGranted = permissionRequestResultGranted,
            notificationsEnabled = notificationsEnabled,
            shouldShowRationale = shouldShowRationale,
            settingsRepository = settingsRepository
        )
    }
}

class NotificationsPermissionState(
    notificationsEnabled: Boolean,
    notificationsPermissionGranted: Boolean,
    permissionRequestResultGranted: Boolean?,
    shouldShowRationale: Boolean,
    private val settingsRepository: SettingsRepository
) {

    init {
        if (permissionRequestResultGranted == false && notificationsEnabled && !shouldShowRationale) {
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    var notificationsPermissionGranted by mutableStateOf(notificationsPermissionGranted)
        private set
    var showNotificationsRationaleDialog by mutableStateOf(
        shouldShowRationale && notificationsEnabled
    )
    var launchNotificationsPermissionRequest by mutableStateOf(
        !notificationsPermissionGranted && !shouldShowRationale
    )
        private set

    fun onNotificationsPermissionGrant() {
        launchNotificationsPermissionRequest = true
    }

    fun onNotificationsPermissionDeny() {
        launchNotificationsPermissionRequest = false
        settingsRepository.setNotificationsEnabled(false)
    }
}