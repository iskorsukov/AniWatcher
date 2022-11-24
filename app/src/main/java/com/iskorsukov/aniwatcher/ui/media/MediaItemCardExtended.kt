package com.iskorsukov.aniwatcher.ui.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.iskorsukov.aniwatcher.test.description
import com.iskorsukov.aniwatcher.test.meanScore
import com.iskorsukov.aniwatcher.test.ranking
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
            .height(200.dp)
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
                genresFooter,
                followButton,
                rankScore,
                description
            ) = createRefs()

            val imageEndGuideline = createGuidelineFromStart(0.35f)
            val titleOverlayTopGuideline = createGuidelineFromBottom(0.25f)
            val genresFooterTopGuideline = createGuidelineFromBottom(0.15f)

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

            HtmlText(
                text = mediaItem.description.orEmpty(),
                style = LocalTextStyles.current.contentSmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(description) {
                        bottom.linkTo(genresFooter.top)
                        start.linkTo(imageEndGuideline)

                        if (airingScheduleItem == null && mediaItem.seasonRanking == null && mediaItem.meanScore == null) {
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

            if (mediaItem.seasonRanking != null || mediaItem.meanScore != null) {
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
                    if (mediaItem.seasonRanking != null) {
                        MediaItemIndicatorWithText(
                            iconResId = R.drawable.ic_outline_favorite_border_24_white,
                            iconTint = LocalColors.current.cardIndicatorLow,
                            text = "#${mediaItem.seasonRanking.rank}"
                        )
                    }
                }
            }

            MediaItemGenresFooter(
                genres = mediaItem.genres,
                colorStr = mediaItem.colorStr,
                modifier = Modifier
                    .background(color = LocalColors.current.cardFooterBackground)
                    .padding(end = 30.dp)
                    .constrainAs(genresFooter) {
                        top.linkTo(genresFooterTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                onGenreChipClicked = onGenreChipClicked
            )

            MediaItemFollowButton(
                isFollowing = mediaItem.isFollowing,
                modifier = Modifier
                    .padding(4.dp)
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
private fun MediaItemIndicatorWithText(
    @DrawableRes iconResId: Int,
    iconTint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = LocalTextStyles.current.contentSmallLarger
        )
    }
}

@Composable
@Preview
fun MediaItemCardExtendedPreview() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50)),
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50)),
        airingScheduleItem = null,
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noRankScore() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50))
            .ranking(null).meanScore(null),
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule_noRankScore() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50))
            .ranking(null).meanScore(null),
        airingScheduleItem = null,
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}