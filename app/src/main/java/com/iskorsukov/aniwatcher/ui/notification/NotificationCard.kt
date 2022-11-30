package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.media.MediaItemAiringInfoColumn
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun NotificationCard(
    notificationItem: NotificationItem,
    timeInMinutes: Long,
    onNotificationClicked: ((Int) -> Unit)? = null,
    preferredNamingScheme: NamingScheme
) {
    val airingScheduleItem = notificationItem.airingScheduleItem
    val mediaItem = notificationItem.airingScheduleItem.mediaItem
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onNotificationClicked?.invoke(mediaItem.id) },
        elevation = 4.dp,
        backgroundColor = LocalColors.current.cardBackground
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
                MediaItemAiringInfoColumn(
                    airingScheduleItem = airingScheduleItem,
                    timeInMinutes = timeInMinutes
                )
            }
        }
    }
}

@Composable
@Preview
private fun NotificationCardPreview() {
    NotificationCard(
        notificationItem = ModelTestDataCreator.baseNotificationItem(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH
    )
}