package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.iskorsukov.aniwatcher.ui.base.text.HtmlText
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun MediaItemCardExtended(
    mediaItem: MediaItem,
    airingScheduleItem: AiringScheduleItem?,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: ((String) -> Unit)? = null,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .heightIn(min = 220.dp)
            .fillMaxWidth()
            .clickable { onMediaClicked.invoke(mediaItem) },
        elevation = 8.dp,
        backgroundColor = LocalColors.current.cardBackground
    ) {
        ConstraintLayout {
            val (
                image,
                format,
                titleOverlay,
                cardContent,
                footerBackground,
                genresFooter,
                followButton,
                rankScore,
                description
            ) = createRefs()

            val imageEndGuideline = createGuidelineFromStart(0.35f)
            val titleOverlayTopGuideline = createGuidelineFromBottom(0.30f)

            MediaItemImage(
                imageUrl = mediaItem.coverImageUrl,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
            )

            if (mediaItem.format != null && mediaItem.format != MediaItem.LocalFormat.TV) {
                MediaFormatText(
                    text = stringResource(id = mediaItem.format.labelResId),
                    modifier = Modifier
                        .padding(4.dp)
                        .constrainAs(format) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
            }

            MediaItemOverlayTitle(
                title = mediaItem.title.baseText(preferredNamingScheme),
                modifier = Modifier
                    .background(color = LocalColors.current.titleOverlay)
                    .padding(4.dp)
                    .constrainAs(titleOverlay) {
                        top.linkTo(titleOverlayTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.start)
                        end.linkTo(image.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )

            if (mediaItem.status == MediaItem.LocalStatus.FINISHED) {
                Text(
                    text = stringResource(id = R.string.media_finished_airing),
                    style = LocalTextStyles.current.contentSmallLargerEmphasis,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .constrainAs(cardContent) {
                            top.linkTo(parent.top)
                            start.linkTo(imageEndGuideline)
                            end.linkTo(rankScore.start)

                            width = Dimension.fillToConstraints
                        }
                )
            } else {
                MediaItemAiringInfoColumn(
                    airingScheduleItem = airingScheduleItem,
                    timeInMinutes = timeInMinutes,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                        .constrainAs(cardContent) {
                            top.linkTo(parent.top)
                            start.linkTo(imageEndGuideline)
                            end.linkTo(rankScore.start)

                            width = Dimension.fillToConstraints
                        }
                )
            }

            HtmlText(
                text = mediaItem.description.orEmpty(),
                style = LocalTextStyles.current.contentSmallLarger,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(description) {
                        bottom.linkTo(genresFooter.top)
                        start.linkTo(imageEndGuideline)

                        if (airingScheduleItem == null && mediaItem.popularity == null && mediaItem.meanScore == null) {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(parent.end)
                        } else if (airingScheduleItem != null) {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(parent.end)
                        } else {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(rankScore.start)
                        }

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )

            if (mediaItem.popularity != null || mediaItem.meanScore != null) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .constrainAs(rankScore) {
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

            val footerBackgroundTopBarrier = createTopBarrier(genresFooter, followButton)

            Box(
                modifier = Modifier
                    .background(color = LocalColors.current.cardFooterBackground)
                    .constrainAs(footerBackground) {
                        top.linkTo(footerBackgroundTopBarrier)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )

            MediaItemGenresFooter(
                genres = mediaItem.genres,
                colorStr = mediaItem.colorStr,
                modifier = Modifier
                    .constrainAs(genresFooter) {
                        top.linkTo(footerBackgroundTopBarrier)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.end)
                        end.linkTo(followButton.start)

                        width = Dimension.fillToConstraints
                    },
                onGenreChipClicked = onGenreChipClicked
            )

            MediaItemFollowButton(
                isFollowing = mediaItem.isFollowing,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .constrainAs(followButton) {
                        end.linkTo(parent.end)
                        top.linkTo(footerBackgroundTopBarrier)
                        bottom.linkTo(parent.bottom)
                    },
                onFollowClicked = { onFollowClicked(mediaItem) }
            )
        }
    }
}

@Composable
private fun MediaItemOverlayTitle(
    title: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = title,
            style = LocalTextStyles.current.onTitleOverlay,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview
fun MediaItemCardExtendedPreview() {
    val mediaItemWithLongDescription = ModelTestDataCreator.previewData().first
        .copy(description = "Word ".repeat(50))
    MediaItemCardExtended(
        mediaItem = mediaItemWithLongDescription,
        airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedFinishedAiringPreview() {
    val mediaItemWithLongDescription = ModelTestDataCreator.previewData().first
        .copy(description = "Word ".repeat(50))
    MediaItemCardExtended(
        mediaItem = mediaItemWithLongDescription.copy(status = MediaItem.LocalStatus.FINISHED),
        airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule() {
    val mediaItemWithLongDescription = ModelTestDataCreator.previewData().first
        .copy(description = "Word ".repeat(50))
    MediaItemCardExtended(
        mediaItem = mediaItemWithLongDescription,
        airingScheduleItem = null,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noRankScore() {
    val mediaItemLongDescriptionNoRankScore = ModelTestDataCreator.previewData().first
        .copy(
            description = "Word ".repeat(50),
            popularity = null,
            meanScore = null
        )
    MediaItemCardExtended(
        mediaItem = mediaItemLongDescriptionNoRankScore,
        airingScheduleItem = ModelTestDataCreator.previewData().second.first(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule_noRankScore() {
    val mediaItemLongDescriptionNoRankScore = ModelTestDataCreator.previewData().first
        .copy(
            description = "Word ".repeat(50),
            popularity = null,
            meanScore = null
        )
    MediaItemCardExtended(
        mediaItem = mediaItemLongDescriptionNoRankScore,
        airingScheduleItem = null,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}