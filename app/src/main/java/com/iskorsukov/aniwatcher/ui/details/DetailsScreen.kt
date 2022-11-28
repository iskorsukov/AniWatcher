package com.iskorsukov.aniwatcher.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.bannerImage
import com.iskorsukov.aniwatcher.test.coverImageUrl
import com.iskorsukov.aniwatcher.ui.base.text.HtmlText
import com.iskorsukov.aniwatcher.ui.media.MediaItemAiringInfoColumn
import com.iskorsukov.aniwatcher.ui.media.MediaItemImage
import com.iskorsukov.aniwatcher.ui.theme.*
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailsScreen(
    timeInMinutesFlow: Flow<Long>,
    mediaItem: MediaItem,
    airingScheduleList: List<AiringScheduleItem>?,
    modifier: Modifier = Modifier,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH,
    onLearnMoreClicked: (String) -> Unit
) {

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    DetailScreenContent(
        mediaItem = mediaItem,
        airingScheduleList = airingScheduleList,
        timeInMinutes = timeInMinutes,
        modifier = modifier,
        preferredNamingScheme = preferredNamingScheme,
        onLearnMoreClicked = onLearnMoreClicked
    )
}

@Composable
private fun DetailScreenContent(
    mediaItem: MediaItem,
    airingScheduleList: List<AiringScheduleItem>?,
    timeInMinutes: Long,
    modifier: Modifier = Modifier,
    preferredNamingScheme: NamingScheme,
    onLearnMoreClicked: (String) -> Unit
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (
            bannerImage,
            coverImage,
            contentBody,
            mediaInfo,
            mediaInfoBackground
        ) = createRefs()

        val separatorGuideline = createGuidelineFromStart(0.35f)

        val mediaInfoBackgroundTopBarrier = createBottomBarrier(bannerImage)
        val mediaInfoContentTopBarrier = createBottomBarrier(bannerImage, coverImage)
        val coverImageTopBarrier = createBottomBarrier(bannerImage, margin = (-40).dp)
        val contentBodyTopBarrier = createBottomBarrier(bannerImage)

        if (mediaItem.bannerImageUrl != null) {
            MediaItemImage(
                imageUrl = mediaItem.bannerImageUrl,
                modifier = Modifier
                    .height(100.dp)
                    .constrainAs(bannerImage) {
                        top.linkTo(parent.top)

                        width = Dimension.matchParent
                    }
            )
        }

        Box(
            modifier = Modifier
                .background(LocalColors.current.backgroundSecondary)
                .constrainAs(mediaInfoBackground) {
                    start.linkTo(parent.start)
                    end.linkTo(separatorGuideline)
                    top.linkTo(mediaInfoBackgroundTopBarrier)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )


        if (mediaItem.coverImageUrl != null) {
            MediaItemImage(
                imageUrl = mediaItem.coverImageUrl,
                modifier = Modifier
                    .height(160.dp)
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .constrainAs(coverImage) {
                        start.linkTo(parent.start)
                        end.linkTo(separatorGuideline)
                        top.linkTo(coverImageTopBarrier)

                        width = Dimension.fillToConstraints
                    }
            )
        }

        DetailsMediaInfoColumn(
            mediaItem = mediaItem,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .constrainAs(mediaInfo) {
                    start.linkTo(parent.start)
                    end.linkTo(separatorGuideline)
                    top.linkTo(mediaInfoContentTopBarrier)

                    width = Dimension.fillToConstraints
                }
        )

        DetailsContentLazyColumn(
            mediaItem = mediaItem,
            preferredNamingScheme = preferredNamingScheme,
            airingScheduleList = airingScheduleList,
            timeInMinutes = timeInMinutes,
            onLearnMoreClicked = onLearnMoreClicked,
            modifier = Modifier
                .constrainAs(contentBody) {
                    top.linkTo(contentBodyTopBarrier)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(separatorGuideline)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
private fun DetailsContentLazyColumn(
    mediaItem: MediaItem,
    preferredNamingScheme: NamingScheme,
    airingScheduleList: List<AiringScheduleItem>?,
    timeInMinutes: Long,
    modifier: Modifier = Modifier,
    onLearnMoreClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            DetailsTitleDescriptionColumn(
                mediaItem = mediaItem,
                preferredNamingScheme = preferredNamingScheme
            )
        }
        item {
            if (mediaItem.siteUrl != null) {
                Text(
                    text = stringResource(id = R.string.media_info_learn_more).uppercase(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            LocalColors.current.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onLearnMoreClicked.invoke(mediaItem.siteUrl)
                        }
                        .padding(8.dp)
                )
            }
        }
        if (airingScheduleList?.isNotEmpty() == true) {
            item {
                Text(
                    text = stringResource(id = R.string.media_info_airing_schedule),
                    style = LocalTextStyles.current.headlineEmphasis,
                    modifier =  Modifier.padding(top = 4.dp)
                )
            }
            airingScheduleList.map {
                item {
                    DetailsAiringScheduleCard(
                        airingScheduleItem = it,
                        timeInMinutes = timeInMinutes
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun DetailsContentLazyColumnPreview() {
    DetailsContentLazyColumn(
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onLearnMoreClicked = { }
    )
}

@Composable
private fun DetailsTitleDescriptionColumn(
    mediaItem: MediaItem,
    preferredNamingScheme: NamingScheme,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = mediaItem.title.baseText(preferredNamingScheme),
            style = LocalTextStyles.current.headlineEmphasis
        )
        if (mediaItem.title.subText().isNotEmpty()) {
            Text(
                text = mediaItem.title.subText(preferredNamingScheme),
                style = LocalTextStyles.current.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        HtmlText(
            text = mediaItem.description.orEmpty(),
            style = LocalTextStyles.current.contentSmallLarger
        )
    }
}

@Composable
@Preview
private fun DetailsTitleDescriptionColumPreview() {
    DetailsTitleDescriptionColumn(
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        preferredNamingScheme = NamingScheme.ENGLISH
    )
}

@Composable
private fun DetailsAiringScheduleCard(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        backgroundColor = LocalColors.current.cardBackground,
        elevation = 4.dp
    ) {
        MediaItemAiringInfoColumn(
            airingScheduleItem = airingScheduleItem,
            timeInMinutes = timeInMinutes,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
@Preview
private fun DetailsAiringScheduleCardPreview() {
    DetailsAiringScheduleCard(
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
    )
}

@Composable
private fun DetailsMediaInfoColumn(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (mediaItem.format != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_format),
                subLabel = stringResource(id = mediaItem.format.labelResId)
            )
        }
        if (mediaItem.mainStudio != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_studio),
                subLabel = mediaItem.mainStudio
            )
        }
        if (mediaItem.popularity != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_rank_in_season),
                subLabel = "${mediaItem.popularity}"
            )
        }
        if (mediaItem.meanScore != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_mean_score),
                subLabel = "${mediaItem.meanScore}%"
            )
        }
        if (mediaItem.genres.isNotEmpty()) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_genres),
                subLabel = mediaItem.genres.joinToString(separator = ", ")
            )
        }
    }
}

@Composable
@Preview
private fun DetailsMediaInfoColumnPreview() {
    DetailsMediaInfoColumn(
        mediaItem = ModelTestDataCreator.baseMediaItem()
    )
}

@Composable
private fun DetailsMediaInfoItem(
    label: String,
    subLabel: String
) {
    Text(
        text = label,
        style = LocalTextStyles.current.contentSmallEmphasis,
        modifier = Modifier.padding(top = 8.dp)
    )
    Text(
        text = subLabel,
        style = LocalTextStyles.current.contentSmallLarger
    )
}

@Composable
@Preview
private fun DetailsScreenPreview() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onLearnMoreClicked = { }
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noBanner() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem().bannerImage(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onLearnMoreClicked = { }
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noCoverImage() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem().coverImageUrl(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onLearnMoreClicked = { }
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noBannerOrImage() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem().bannerImage(null).coverImageUrl(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onLearnMoreClicked = { }
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noAiringSchedule() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        airingScheduleList = emptyList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onLearnMoreClicked = { }
    )
}