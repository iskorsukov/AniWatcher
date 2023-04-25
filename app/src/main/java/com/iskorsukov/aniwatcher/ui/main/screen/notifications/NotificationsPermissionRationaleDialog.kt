package com.iskorsukov.aniwatcher.ui.main.screen.notifications

import androidx.compose.runtime.Composable
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.base.permission.PermissionRationaleDialog
import com.iskorsukov.aniwatcher.ui.main.state.NotificationsPermissionState

@Composable
fun NotificationsPermissionRationaleDialog(
    notificationsPermissionState: NotificationsPermissionState
) {
    PermissionRationaleDialog(
        titleResId = R.string.notifications_permission_title,
        textResId = R.string.notifications_permission_text,
        denyTextResId = R.string.notifications_permission_disable,
        onPermissionGranted = {
            notificationsPermissionState.onNotificationsPermissionGranted()
        },
        onPermissionDenied = {
            notificationsPermissionState.onNotificationsPermissionDenied()
        },
        onDismissRequest = {
            notificationsPermissionState.onNotificationsPermissionDenied()
        }
    )
}