package com.iskorsukov.aniwatcher.ui.main.screen.notifications

import androidx.compose.runtime.Composable
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.base.permission.PermissionRationaleDialog
import com.iskorsukov.aniwatcher.ui.main.state.NotificationsRationaleDialogState

@Composable
fun NotificationsPermissionRationaleDialog(
    notificationsRationaleDialogState: NotificationsRationaleDialogState
) {
    PermissionRationaleDialog(
        titleResId = R.string.notifications_permission_title,
        textResId = R.string.notifications_permission_text,
        denyTextResId = R.string.cancel,
        onPermissionGrant = {
            notificationsRationaleDialogState.launchRequest()
        },
        onPermissionDeny = {},
        onDismissRequest = {
            notificationsRationaleDialogState.dismiss()
        }
    )
}