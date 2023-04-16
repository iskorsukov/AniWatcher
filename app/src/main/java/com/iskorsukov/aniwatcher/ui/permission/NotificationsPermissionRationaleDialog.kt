package com.iskorsukov.aniwatcher.ui.permission

import androidx.compose.runtime.Composable
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.base.permission.PermissionRationaleDialog

@Composable
fun NotificationsPermissionRationaleDialog(
    onNotificationsPermissionGranted: () -> Unit,
    onNotificationsPermissionDenied: () -> Unit,
    onDismissRequest: () -> Unit
) {
    PermissionRationaleDialog(
        titleResId = R.string.notifications_permission_title,
        textResId = R.string.notifications_permission_text,
        denyTextResId = R.string.notifications_permission_disable,
        onPermissionGranted = onNotificationsPermissionGranted,
        onPermissionDenied = onNotificationsPermissionDenied,
        onDismissRequest = onDismissRequest
    )
}