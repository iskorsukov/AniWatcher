package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataFullscreenPlaceholder
import com.iskorsukov.aniwatcher.ui.media.MediaItemAiringInfoColumn
import com.iskorsukov.aniwatcher.ui.theme.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    modifier: Modifier,
    onNotificationClicked: ((Int) -> Unit)? = null
) {
    val settingsState by notificationsViewModel.settingsState
        .collectAsStateWithLifecycle()

    val notificationsList by notificationsViewModel.notificationsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    Box(modifier = modifier.fillMaxSize()) {
        if (notificationsList.isEmpty()) {
            EmptyDataFullscreenPlaceholder(
                iconResId = R.drawable.ic_baseline_notifications_none_24,
                labelResId = R.string.notifications_data_empty_label,
                subLabelResId = R.string.notifications_data_empty_sub_label
            )
        } else {
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
    }
}

@Composable
fun NotificationCard(
    notificationItem: NotificationItem,
    onNotificationClicked: ((Int) -> Unit)? = null,
    preferredNamingScheme: NamingScheme
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
                    style = LocalTextStyles.current.contentSmallLarger,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                MediaItemAiringInfoColumn(airingScheduleItem = airingScheduleItem)
            }
        }
    }
}