package com.iskorsukov.aniwatcher.ui.main.state

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberNotificationsRationaleDialogState(
    requestLauncher: ActivityResultLauncher<String>
): NotificationsRationaleDialogState {
    return remember {
        NotificationsRationaleDialogState(requestLauncher)
    }
}

class NotificationsRationaleDialogState(
    private val requestLauncher: ActivityResultLauncher<String>
) {

    var showNotificationsRationaleDialog by mutableStateOf(false)
        private set

    fun launchRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun show() {
        showNotificationsRationaleDialog = true
    }

    fun dismiss() {
        showNotificationsRationaleDialog = false
    }
}