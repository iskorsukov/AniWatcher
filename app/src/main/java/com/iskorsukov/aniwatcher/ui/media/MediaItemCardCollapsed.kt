package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.ui.theme.ContentTextStyleSmallLarger
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun MediaItemCardCollapsed(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme
) {
    val mediaItem = airingScheduleItem.mediaItem
    Card(
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .height(76.dp)
            .fillMaxWidth()
            .clickable { onMediaClicked.invoke(mediaItem) },
        elevation = 4.dp,
        backgroundColor = LocalColors.current.cardBackground
    ) {
        ConstraintLayout {
            val (image, format, cardContent, followButton) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.2f)

            MediaItemImage(
                imageUrl = mediaItem.coverImageUrl,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)
                    height = Dimension.matchParent
                    width = Dimension.fillToConstraints
                }
            )

            if (mediaItem.format != null && mediaItem.format != MediaItem.LocalFormat.TV) {
                MediaFormatText(
                    text = stringResource(id = mediaItem.format.labelResId),
                    isRounded = false,
                    modifier = Modifier
                        .constrainAs(format) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(image.start)
                            end.linkTo(image.end)

                            width = Dimension.fillToConstraints
                        }
                )
            }

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
                    style = LocalTextStyles.current.contentSmallLargerEmphasis,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                MediaItemAiringInfoColumn(
                    airingScheduleItem = airingScheduleItem,
                    timeInMinutes = timeInMinutes
                )
            }

            MediaItemFollowButton(
                isFollowing = mediaItem.isFollowing,
                modifier = Modifier
                    .padding(8.dp)
                    .size(22.dp)
                    .constrainAs(followButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                onFollowClicked = { onFollowClicked(mediaItem) }
            )
        }
    }
}