package com.iskorsukov.aniwatcher.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
    val mediaItem = notificationItem.mediaItem
    Card(
        modifier = Modifier
            .heightIn(min = 100.dp)
            .fillMaxWidth()
            .padding(top = 8.dp)
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
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        start.linkTo(imageEndGuideline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    }
            ) {
                Text(
                    text = mediaItem.title.baseText(preferredNamingScheme),
                    style = LocalTextStyles.current.contentSmallLargerEmphasis,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
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
        notificationItem = ModelTestDataCreator.notificationItem(
            id = 1,
            airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
            mediaItem = ModelTestDataCreator.previewData().first
        ),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH
    )
}