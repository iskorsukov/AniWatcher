package com.iskorsukov.aniwatcher.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.bannerImage
import com.iskorsukov.aniwatcher.test.coverImageUrl
import com.iskorsukov.aniwatcher.ui.base.effects.horizontalFadingEdge
import com.iskorsukov.aniwatcher.ui.base.effects.verticalFadingEdge
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
    onBackButtonClicked: () -> Unit = { },
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
        onBackButtonClicked = onBackButtonClicked,
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
    onBackButtonClicked: () -> Unit,
    onLearnMoreClicked: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .verticalFadingEdge(
                lazyListState = lazyListState,
                length = 100.dp,
                edgeColor = LocalColors.current.background,
                topEdge = false
            ),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item {
            DetailsContentHeader(
                mediaItem = mediaItem,
                preferredNamingScheme = preferredNamingScheme,
                onBackButtonClicked = onBackButtonClicked,
                onLearnMoreClicked = onLearnMoreClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            HtmlText(
                text = mediaItem.description.orEmpty(),
                style = LocalTextStyles.current.contentMedium,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )
        }
        if (airingScheduleList?.isNotEmpty() == true) {
            item {
                Text(
                    text = stringResource(id = R.string.media_info_airing_schedule),
                    style = LocalTextStyles.current.headlineEmphasis,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                )
            }
            airingScheduleList.map {
                item {
                    DetailsAiringScheduleCard(
                        airingScheduleItem = it,
                        timeInMinutes = timeInMinutes,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsContentHeader(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
    preferredNamingScheme: NamingScheme,
    onBackButtonClicked: () -> Unit,
    onLearnMoreClicked: (String) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (
            bannerImage,
            backButton,
            coverImage,
            mediaInfo,
            mediaInfoBackground
        ) = createRefs()

        MediaItemImage(
            imageUrl = mediaItem.bannerImageUrl,
            modifier = Modifier
                .height(100.dp)
                .constrainAs(bannerImage) {
                    top.linkTo(parent.top)

                    width = Dimension.matchParent
                    visibility = if (mediaItem.bannerImageUrl == null)
                        Visibility.Gone
                    else
                        Visibility.Visible
                }
        )


        if (mediaItem.bannerImageUrl != null) {
            IconButton(
                onClick = onBackButtonClicked,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(backButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary,
                    modifier = Modifier
                        .background(
                            color = LocalColors.current.titleOverlay,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .background(LocalColors.current.backgroundSecondary)
                .constrainAs(mediaInfoBackground) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(bannerImage.bottom)
                    bottom.linkTo(mediaInfo.bottom)

                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )

        MediaItemImage(
            imageUrl = mediaItem.coverImageUrl,
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .constrainAs(coverImage) {
                    start.linkTo(parent.start)
                    top.linkTo(bannerImage.bottom, (-40).dp)
                    bottom.linkTo(mediaInfo.bottom)

                    height = Dimension.fillToConstraints

                    visibility = if (mediaItem.coverImageUrl == null)
                        Visibility.Gone
                    else
                        Visibility.Visible
                }
        )

        DetailsMediaInfo(
            mediaItem = mediaItem,
            preferredNamingScheme = preferredNamingScheme,
            onLearnMoreClicked = onLearnMoreClicked,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(mediaInfo) {
                    start.linkTo(coverImage.end)
                    end.linkTo(parent.end)
                    top.linkTo(bannerImage.bottom)

                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
private fun DetailsAiringScheduleCard(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
private fun DetailsMediaInfo(
    mediaItem: MediaItem,
    preferredNamingScheme: NamingScheme,
    modifier: Modifier = Modifier,
    onLearnMoreClicked: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()
    Column(modifier = modifier) {
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
        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalFadingEdge(
                lazyListState,
                36.dp,
                LocalColors.current.backgroundSecondary
            )
        ) {
            if (mediaItem.format != null) {
                item {
                    DetailsMediaInfoCard(
                        label = stringResource(id = R.string.media_info_format),
                        subLabel = stringResource(id = mediaItem.format.labelResId)
                    )
                }
            }
            if (mediaItem.mainStudio != null) {
                item {
                    DetailsMediaInfoCard(
                        label = stringResource(id = R.string.media_info_studio),
                        subLabel = mediaItem.mainStudio
                    )
                }
            }
            if (mediaItem.meanScore != null) {
                item {
                    DetailsMediaInfoCard(
                        label = stringResource(id = R.string.media_info_mean_score),
                        subLabel = "${mediaItem.meanScore}%"
                    )
                }
            }
            if (mediaItem.genres.isNotEmpty()) {
                item {
                    DetailsMediaInfoCard(
                        label = stringResource(id = R.string.media_info_genres),
                        subLabel = mediaItem.genres.joinToString(separator = ", ")
                    )
                }
            }
            if (mediaItem.popularity != null) {
                item {
                    DetailsMediaInfoCard(
                        label = stringResource(id = R.string.media_info_rank_in_season),
                        subLabel = "${mediaItem.popularity}"
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsMediaInfoCard(
    label: String,
    subLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = LocalColors.current.background,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = label,
                style = LocalTextStyles.current.contentSmallLargerEmphasis
            )
            Text(
                text = subLabel,
                style = LocalTextStyles.current.contentMedium
            )
        }
    }
}

@Composable
@Preview
private fun DetailsScreenPreview() {
    DetailScreenContent(
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        mediaItem = ModelTestDataCreator.baseMediaItem(),
        airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList(),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onBackButtonClicked = { },
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
        onBackButtonClicked = { },
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
        onBackButtonClicked = { },
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
        onBackButtonClicked = { },
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
        onBackButtonClicked = { },
        onLearnMoreClicked = { }
    )
}