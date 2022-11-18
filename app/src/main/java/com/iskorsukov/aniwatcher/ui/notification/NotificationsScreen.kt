package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    onNotificationClicked: ((Int) -> Unit)? = null,
) {
    val settingsState by notificationsViewModel.settingsState
        .collectAsStateWithLifecycle()

    val notificationsList by notificationsViewModel.notificationsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    if (notificationsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_notifications_none_24
                    ),
                    contentDescription = null,
                    tint = CardTextColorLight,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.notifications_data_empty_label
                    ),
                    color = CardTextColorLight,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(
                        id = R.string.notifications_data_empty_sub_label
                    ),
                    color = CardTextColorLight,
                    fontSize = 12.sp
                )
            }
        }
    }
    LazyColumn {
        notificationsList.forEach {
            item {
                NotificationCard(
                    notificationItem = it,
                    preferredNamingScheme = settingsState.preferredNamingScheme,
                    onNotificationClicked = onNotificationClicked
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    notificationItem: NotificationItem,
    onNotificationClicked: ((Int) -> Unit)? = null,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH
) {
    val mediaItem = notificationItem.mediaItem
    val airingScheduleItem = notificationItem.airingScheduleItem
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNotificationClicked?.invoke(mediaItem.id) },
        elevation = 10.dp
    ) {
        ConstraintLayout {
            val (image, cardContent) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.2f)

            AsyncImage(
                model = mediaItem.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)
                    height = Dimension.matchParent
                    width = Dimension.fillToConstraints
                }
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        start.linkTo(imageEndGuideline)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.matchParent
                    }
            ) {
                Text(
                    text = mediaItem.title.baseText(preferredNamingScheme),
                    color = CardTextColorLight,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = String.format(stringResource(id = R.string.episode_aired_label), airingScheduleItem.episode),
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
                Text(
                    text = "at ${airingScheduleItem.getAiringAtDateTimeFormatted()}",
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
        }
    }
}