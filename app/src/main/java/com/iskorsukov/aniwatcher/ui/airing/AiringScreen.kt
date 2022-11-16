package com.iskorsukov.aniwatcher.ui.airing

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AiringScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: AiringViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>,
    onMediaClicked: ((Int) -> Unit)? = null
) {
    val airingScheduleItemList by viewModel
        .airingSchedulesByDayOfWeekFlow.collectAsStateWithLifecycle(initialValue = emptyMap())

    val uiState by mainActivityViewModel
        .uiState.collectAsStateWithLifecycle()

    val settingsState by mainActivityViewModel
        .settingsState.collectAsStateWithLifecycle()

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    SwipeRefresh(state = swipeRefreshState, onRefresh = { mainActivityViewModel.loadAiringData() }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                            timeInMinutes = timeInMinutes,
                            onFollowClicked = viewModel::onFollowClicked,
                            onMediaClicked = onMediaClicked,
                            preferredNamingScheme = settingsState.preferredNamingScheme
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
                        timeInMinutes = timeInMinutes,
                        onFollowClicked = {},
                        onMediaClicked = {}
                    )
                }
            }
        }
    }
}