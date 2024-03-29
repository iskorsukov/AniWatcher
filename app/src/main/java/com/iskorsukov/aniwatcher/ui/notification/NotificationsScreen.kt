package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    modifier: Modifier,
    onNotificationClicked: (Int) -> Unit
) {
    val settingsState by notificationsViewModel.settingsState
        .collectAsStateWithLifecycle()

    val notificationsUiState by notificationsViewModel.uiState
        .collectAsStateWithLifecycle()

    NotificationsScreenContent(
        notificationsList = notificationsUiState.notifications,
        timeInMinutes = notificationsUiState.timeInMinutes,
        preferredNamingScheme = settingsState.preferredNamingScheme,
        onNotificationClicked = onNotificationClicked,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun NotificationsScreenContent(
    notificationsList: List<NotificationItem>,
    timeInMinutes: Long,
    preferredNamingScheme: NamingScheme,
    onNotificationClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (notificationsList.isEmpty()) {
            EmptyDataPlaceholder(
                iconResId = R.drawable.ic_baseline_notifications_none_24,
                labelResId = R.string.notifications_data_empty_label,
                subLabelResId = R.string.notifications_data_empty_sub_label,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                notificationsList.forEach {
                    item {
                        NotificationCard(
                            notificationItem = it,
                            timeInMinutes = timeInMinutes,
                            preferredNamingScheme = preferredNamingScheme,
                            onNotificationClicked = onNotificationClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun NotificationsScreenPreview() {
    NotificationsScreenContent(
        notificationsList = listOf(
            ModelTestDataCreator.notificationItem(
                id = 1,
                airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
                mediaItem = ModelTestDataCreator.previewData().first
            )
        ),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH,
        onNotificationClicked = { }
    )
}

@Composable
@Preview
private fun NotificationsScreenEmptyPreview() {
    NotificationsScreenContent(
        notificationsList = emptyList(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH,
        onNotificationClicked = { }
    )
}