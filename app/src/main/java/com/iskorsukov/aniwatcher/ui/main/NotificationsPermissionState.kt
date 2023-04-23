package com.iskorsukov.aniwatcher.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberNotificationsPermissionState(
    context: Context,
    settingsRepository: SettingsRepository,
    permissionRequestResultGranted: Boolean?,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): NotificationsPermissionState {
    val notificationsPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || permissionRequestResultGranted == true
    return remember(
        context,
        settingsRepository,
        permissionRequestResultGranted
    ) {
        NotificationsPermissionState(
            notificationsPermissionGranted = notificationsPermissionGranted,
            settingsRepository = settingsRepository,
            coroutineScope = coroutineScope
        )
    }
}

class NotificationsPermissionState(
    coroutineScope: CoroutineScope,
    notificationsPermissionGranted: Boolean,
    private val settingsRepository: SettingsRepository
) {

    init {
        if (!notificationsPermissionGranted) {
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    var notificationsPermissionGranted by mutableStateOf(notificationsPermissionGranted)
        private set
    var showNotificationsRationaleDialog = settingsRepository.settingsStateFlow
        .map { it.notificationsEnabled && !notificationsPermissionGranted }
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            !notificationsPermissionGranted
        )
    var launchNotificationsPermissionRequest by mutableStateOf(false)
        private set

    fun onNotificationsPermissionGranted() {
        launchNotificationsPermissionRequest = false
        notificationsPermissionGranted = true
        settingsRepository.setNotificationsEnabled(true)
    }

    fun onNotificationsPermissionDenied() {
        launchNotificationsPermissionRequest = false
        notificationsPermissionGranted = false
        settingsRepository.setNotificationsEnabled(false)
    }
}