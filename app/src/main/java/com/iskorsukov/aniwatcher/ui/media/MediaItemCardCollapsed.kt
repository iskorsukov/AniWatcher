package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun MediaItemCardCollapsed(
    airingScheduleItem: AiringScheduleItem,
    mediaItem: MediaItem,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .heightIn(min = 100.dp)
            .fillMaxWidth()
            .clickable { onMediaClicked.invoke(mediaItem) },
        elevation = 4.dp,
        backgroundColor = LocalColors.current.cardBackground
    ) {
        ConstraintLayout {
            val (
                image,
                format,
                popularityScore,
                cardContent,
                followButton
            ) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.25f)

            MediaItemImage(
                imageUrl = mediaItem.coverImageUrl,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
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

            if (mediaItem.popularity != null || mediaItem.meanScore != null) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .constrainAs(popularityScore) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                ) {
                    if (mediaItem.meanScore != null) {
                        val scoreTint = if (mediaItem.meanScore <= 33) {
                            LocalColors.current.cardIndicatorLow
                        } else if (mediaItem.meanScore <= 66) {
                            LocalColors.current.cardIndicatorMedium
                        } else {
                            LocalColors.current.cardIndicatorHigh
                        }
                        MediaItemIndicatorWithText(
                            iconResId = R.drawable.ic_baseline_thumb_up_off_alt_24,
                            iconTint = scoreTint,
                            text = "${mediaItem.meanScore}%"
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    if (mediaItem.popularity != null && mediaItem.popularity <= 100) {
                        MediaItemIndicatorWithText(
                            iconResId = R.drawable.ic_outline_favorite_border_24_white,
                            iconTint = LocalColors.current.cardIndicatorLow,
                            text = "#${mediaItem.popularity}"
                        )
                    }
                }
            }

            val contentEndBarrier = createStartBarrier(popularityScore)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        start.linkTo(imageEndGuideline)
                        end.linkTo(contentEndBarrier)

                        width = Dimension.fillToConstraints
                    }
            ) {
                Text(
                    text = mediaItem.title.baseText(preferredNamingScheme),
                    style = LocalTextStyles.current.contentSmallLargerEmphasis,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                if (mediaItem.status == MediaItem.LocalStatus.FINISHED) {
                    Text(
                        text = stringResource(id = R.string.media_finished_airing),
                        style = LocalTextStyles.current.contentSmallLargerEmphasis,
                    )
                } else {
                    MediaItemAiringInfoColumn(
                        airingScheduleItem = airingScheduleItem,
                        timeInMinutes = timeInMinutes
                    )
                }
            }

            MediaItemFollowButton(
                isFollowing = mediaItem.isFollowing,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .constrainAs(followButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                onFollowClicked = { onFollowClicked(mediaItem) }
            )
        }
    }
}

@Composable
@Preview
private fun MediaItemCardCollapsedPreview() {
    MediaItemCardCollapsed(
        airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
        mediaItem = ModelTestDataCreator.previewData().first,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = { },
        onMediaClicked = { },
        preferredNamingScheme = NamingScheme.ENGLISH
    )
}

@Composable
@Preview
private fun MediaItemCardCollapsedFinishedAiringPreview() {
    MediaItemCardCollapsed(
        airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
        mediaItem = ModelTestDataCreator.previewData().first
            .copy(status = MediaItem.LocalStatus.FINISHED),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = { },
        onMediaClicked = { },
        preferredNamingScheme = NamingScheme.ENGLISH
    )
}