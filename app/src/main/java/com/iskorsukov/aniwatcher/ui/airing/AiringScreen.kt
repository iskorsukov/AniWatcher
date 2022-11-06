package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AiringScreen(airingViewModel: AiringViewModel = viewModel(), timeInMinutesFlow: Flow<Long>) {
    val airingScheduleItemList by airingViewModel
        .airingSchedulesByDayOfWeekFlow.collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

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
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                }
                it.value.map {
                    item {
                        MediaItemCardCollapsed(
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
        MediaItemMapper.groupAiringSchedulesByDayOfWeek(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).toSortedMap().map {
            item {
                Text(
                    text = it.key.name,
                    fontSize = 18.sp,
                    color = CardTextColorLight,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )
            }
            it.value.map {
                item {
                    MediaItemCardCollapsed(
                        airingScheduleItem = it,
                        timeInMinutes = timeInMinutes
                    )
                }
            }
        }
    }
}