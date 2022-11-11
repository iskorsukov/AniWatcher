package com.iskorsukov.aniwatcher.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.bannerImage
import com.iskorsukov.aniwatcher.ui.media.toAnnotatedString
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailsScreen(
    timeInMinutesFlow: Flow<Long>,
    mediaItem: MediaItem,
    airingScheduleList: List<AiringScheduleItem>?
) {

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
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
            AsyncImage(
                model = mediaItem.bannerImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .constrainAs(bannerImage) {
                        top.linkTo(parent.top)

                        width = Dimension.matchParent
                    },
                contentScale = ContentScale.Crop,
            )
        }

        if (mediaItem.coverImageUrl != null) {
            AsyncImage(
                model = mediaItem.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(160.dp)
                    .padding(8.dp)
                    .constrainAs(coverImage) {
                        start.linkTo(parent.start)
                        end.linkTo(separatorGuideline)
                        top.linkTo(coverImageTopBarrier)

                        width = Dimension.fillToConstraints
                    }
            )
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(titleDescription) {
                    top.linkTo(bannerImage.bottom)
                    start.linkTo(separatorGuideline)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = mediaItem.title.baseText(),
                color = CardTextColorLight,
                fontSize = 14.sp
            )
            if (mediaItem.title.subText().isNotEmpty()) {
                Text(
                    text = mediaItem.title.subText(),
                    color = CardTextColorLight,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = HtmlCompat.fromHtml(
                    mediaItem.description.orEmpty(),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toAnnotatedString(),
                color = CardTextColorLight,
                fontSize = 12.sp
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .constrainAs(mediaInfo) {
                    top.linkTo(mediaInfoTopBarrier)
                    start.linkTo(parent.start)
                    end.linkTo(separatorGuideline)

                    width = Dimension.fillToConstraints
                }
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (mediaItem.mainStudio != null) {
                Text(
                    text = stringResource(id = R.string.media_info_studio),
                    color = CardTextColorLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = mediaItem.mainStudio,
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
            if (mediaItem.seasonRanking != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.media_info_rank_in_season),
                    color = CardTextColorLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "#${mediaItem.seasonRanking.rank}",
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
            if (mediaItem.meanScore != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.media_info_mean_score),
                    color = CardTextColorLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "${mediaItem.meanScore}%",
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
            if (mediaItem.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.media_info_genres),
                    color = CardTextColorLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = mediaItem.genres.joinToString(separator = ", "),
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
        }

        if (airingScheduleList?.isNotEmpty() == true) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(airingSchedule) {
                        top.linkTo(titleDescription.bottom)
                        start.linkTo(separatorGuideline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    },
                userScrollEnabled = false,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.media_info_airing_schedule),
                        color = CardTextColorLight,
                        fontSize = 10.sp
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
}

@Composable
fun DetailsAiringScheduleCard(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long
) {
    Card(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = 10.dp
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            val episodeAiringStr = if (airingScheduleItem.airingAt - timeInMinutes * 60 <= 0) {
                stringResource(id = R.string.episode_aired_label)
            } else {
                stringResource(id = R.string.episode_airing_label)
            }
            Text(
                text = String.format(episodeAiringStr, airingScheduleItem.episode),
                color = CardTextColorLight,
                fontSize = 10.sp
            )
            airingScheduleItem.getAiringInFormatted(timeInMinutes)?.let {
                Text(
                    text = it,
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }
            Text(
                text = "at ${airingScheduleItem.getAiringAtDateTimeFormatted()}",
                color = CardTextColorLight,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
@Preview
fun DetailsScreenPreview() {
    val timeInMinutes = flowOf(27785711L)

    DetailsScreen(
        timeInMinutesFlow = timeInMinutes,
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()
    )
}

@Composable
@Preview
fun DetailsScreenPreview_noBanner() {
    val timeInMinutes = flowOf(27785711L)

    DetailsScreen(
        timeInMinutesFlow = timeInMinutes,
        mediaItem = ModelTestDataCreator.baseMediaItem().bannerImage(null),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()
    )
}