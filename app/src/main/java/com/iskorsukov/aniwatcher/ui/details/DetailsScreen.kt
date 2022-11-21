package com.iskorsukov.aniwatcher.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailsScreen(
    timeInMinutesFlow: Flow<Long>,
    mediaItem: MediaItem,
    airingScheduleList: List<AiringScheduleItem>?,
    modifier: Modifier = Modifier,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH
) {

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    DetailScreenContent(
        mediaItem = mediaItem,
        airingScheduleList = airingScheduleList,
        timeInMinutes = timeInMinutes,
        modifier = modifier,
        preferredNamingScheme = preferredNamingScheme
    )
}

@Composable
private fun DetailScreenContent(
    mediaItem: MediaItem,
    airingScheduleList: List<AiringScheduleItem>?,
    timeInMinutes: Long,
    modifier: Modifier = Modifier,
    preferredNamingScheme: NamingScheme
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (bannerImage,
            coverImage,
            titleDescription,
            airingSchedule,
            mediaInfo
        ) = createRefs()

        val separatorGuideline = createGuidelineFromStart(0.35f)

        val mediaInfoTopBarrier = createBottomBarrier(bannerImage, coverImage)
        val coverImageTopBarrier = createBottomBarrier(bannerImage, margin = (-40).dp)

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

        if (mediaItem.coverImageUrl != null) {
            MediaItemImage(
                imageUrl = mediaItem.coverImageUrl,
                modifier = Modifier
                    .height(160.dp)
                    .padding(start = 8.dp, top = 8.dp)
                    .constrainAs(coverImage) {
                        start.linkTo(parent.start)
                        end.linkTo(separatorGuideline)
                        top.linkTo(coverImageTopBarrier)

                        width = Dimension.fillToConstraints
                    }
            )
        }

        DetailsTitleDescriptionColumn(
            mediaItem = mediaItem,
            preferredNamingScheme = preferredNamingScheme,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(titleDescription) {
                    top.linkTo(bannerImage.bottom)
                    start.linkTo(separatorGuideline)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        DetailsMediaInfoColumn(
            mediaItem = mediaItem,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .constrainAs(mediaInfo) {
                    top.linkTo(mediaInfoTopBarrier)
                    start.linkTo(parent.start)
                    end.linkTo(separatorGuideline)

                    width = Dimension.fillToConstraints
                }
        )

        if (airingScheduleList?.isNotEmpty() == true) {
            DetailsAiringSchedulesList(
                airingScheduleList = airingScheduleList,
                timeInMinutes = timeInMinutes,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(airingSchedule) {
                        top.linkTo(titleDescription.bottom)
                        start.linkTo(separatorGuideline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
            )
        }
    }
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
            style = ContentTextStyleMedium
        )
        if (mediaItem.title.subText().isNotEmpty()) {
            Text(
                text = mediaItem.title.subText(preferredNamingScheme),
                style = ContentTextStyleSmallLarger
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HtmlText(
            text = mediaItem.description.orEmpty(),
            style = ContentTextStyleSmallLarger
        )
    }
}

@Composable
private fun DetailsMediaInfoColumn(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (mediaItem.mainStudio != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_studio),
                subLabel = mediaItem.mainStudio
            )
        }
        if (mediaItem.seasonRanking != null) {
            DetailsMediaInfoItem(
                label = stringResource(id = R.string.media_info_rank_in_season),
                subLabel = "#${mediaItem.seasonRanking.rank}"
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
private fun DetailsMediaInfoItem(
    label: String,
    subLabel: String
) {
    Text(
        text = label,
        style = ContentTextStyleSmallEmphasis,
        modifier = Modifier.padding(top = 8.dp)
    )
    Text(
        text = subLabel,
        style = ContentTextStyleSmallLarger
    )
}

@Composable
private fun DetailsAiringSchedulesList(
    airingScheduleList: List<AiringScheduleItem>,
    timeInMinutes: Long,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        userScrollEnabled = false,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.media_info_airing_schedule),
                style = ContentTextStyleSmall
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

@Composable
private fun DetailsAiringScheduleCard(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
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
private fun DetailsScreenPreview() {
    val timeInMinutes = flowOf(27785711L)

    DetailsScreen(
        timeInMinutesFlow = timeInMinutes,
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noBanner() {
    val timeInMinutes = flowOf(27785711L)

    DetailsScreen(
        timeInMinutesFlow = timeInMinutes,
        mediaItem = ModelTestDataCreator.baseMediaItem().bannerImage(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()
    )
}

@Composable
@Preview
private fun DetailsScreenPreview_noBannerOrImage() {
    val timeInMinutes = flowOf(27785711L)

    DetailsScreen(
        timeInMinutesFlow = timeInMinutes,
        mediaItem = ModelTestDataCreator.baseMediaItem().bannerImage(null).coverImageUrl(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()
    )
}