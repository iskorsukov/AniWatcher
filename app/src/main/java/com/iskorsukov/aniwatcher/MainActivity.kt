package com.iskorsukov.aniwatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.domain.mapper.AiringSchedulesMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.airing.AiringViewModel
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.CardFooterBackgroundColor
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import com.iskorsukov.aniwatcher.ui.theme.TitleOverlayColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val airingViewModel: AiringViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AniWatcherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AiringScreen(airingViewModel)
                }
            }
        }
        airingViewModel.loadAiringData()
    }
}


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AiringScreen(airingViewModel: AiringViewModel = viewModel()) {
    val airingScheduleItemList by airingViewModel
        .airingSchedulesByDayOfWeekFlow.collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by airingViewModel
        .timeInMinutesFlow.collectAsStateWithLifecycle(initialValue = 0)

    if (airingScheduleItemList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            airingScheduleItemList.entries.map {
                item {
                    Text(
                        text = it.key.name,
                        fontSize = 20.sp,
                        color = CardTextColorLight,
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )
                }
                it.value.map {
                    item {
                        AiringScheduleItemCardConstraint(
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
@Preview
fun AiringScreenPreview() {
    val timeInMinutes = 27785711L

    LazyColumn {
        AiringSchedulesMapper.groupAiringSchedulesByDayOfWeek(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).toSortedMap().map {
            item {
                Text(
                    text = it.key.name,
                    fontSize = 18.sp,
                    color = CardTextColorLight,
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                )
            }
            it.value.map {
                item {
                    AiringScheduleItemCardConstraint(airingScheduleItem = it, timeInMinutes = timeInMinutes)
                }
            }
        }
    }
}

@Composable
fun AiringScheduleItemCardConstraint(airingScheduleItem: AiringScheduleItem, timeInMinutes: Long) {
    val media = airingScheduleItem.mediaItem
    Card(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 10.dp
    ) {
        ConstraintLayout {
            val (image, titleOverlay, cardContent, genresFooter) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.35f)
            val titleOverlayTopGuideline = createGuidelineFromBottom(0.25f)
            val genresFooterTopGuideline = createGuidelineFromBottom(0.15f)

            AsyncImage(
                model = media.coverImageUrl,
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
                    .background(color = TitleOverlayColor)
                    .padding(4.dp)
                    .constrainAs(titleOverlay) {
                        top.linkTo(titleOverlayTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.start)
                        end.linkTo(image.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = media.title.baseText(),
                    color = Color.White,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        top.linkTo(parent.top)
                        bottom.linkTo(genresFooter.top)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            ) {
                Text(text = "Episode ${airingScheduleItem.episode} airing in", color = CardTextColorLight, fontSize = 10.sp)
                Text(text = airingScheduleItem.getAiringInFormatted(timeInMinutes), color = CardTextColorLight, fontSize = 12.sp)
                Text(text = "at ${airingScheduleItem.getAiringAtFormatted()}", color = CardTextColorLight, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = media.description.orEmpty(), color = CardTextColorLight, fontSize = 10.sp, overflow = TextOverflow.Ellipsis)
            }

            LazyRow(
                modifier = Modifier
                    .background(color = CardFooterBackgroundColor)
                    .constrainAs(genresFooter) {
                        top.linkTo(genresFooterTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                media.genres.map {
                    item {
                        GenreChip(genre = it, colorStr = media.colorStr)
                    }
                }
            }
        }
    }
}

@Composable
fun GenreChip(genre: String, colorStr: String?) {
    Text(
        text = genre,
        color = Color.White,
        fontSize = 8.sp,
        modifier = Modifier
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 4.dp
            )
            .background(
                shape = RoundedCornerShape(10.dp),
                color = if (colorStr == null) {
                    MaterialTheme.colors.primary
                } else {
                    Color(android.graphics.Color.parseColor(colorStr))
                }
            )
            .padding(4.dp)
    )
}