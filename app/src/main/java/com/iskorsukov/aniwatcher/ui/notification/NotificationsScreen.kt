package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder
import com.iskorsukov.aniwatcher.ui.theme.*
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    timeInMinutesFlow: Flow<Long>,
    modifier: Modifier,
    onNotificationClicked: (Int) -> Unit
) {
    val settingsState by notificationsViewModel.settingsState
        .collectAsStateWithLifecycle()

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    val notificationsList by notificationsViewModel.notificationsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    NotificationsScreenContent(
        notificationsList = notificationsList,
        timeInMinutes = timeInMinutes,
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
            LazyColumn {
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
            ModelTestDataCreator.baseNotificationItem()
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