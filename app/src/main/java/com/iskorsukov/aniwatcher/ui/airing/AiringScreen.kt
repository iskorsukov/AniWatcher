package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.theme.CategoryTextStyle
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AiringScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: AiringViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>,
    onMediaClicked: (MediaItem) -> Unit
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

    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            swipeRefreshState = swipeRefreshState,
            onRefresh = mainActivityViewModel::loadAiringData,
            airingSchedulesByDayOfWeekMap = airingScheduleItemList,
            timeInMinutes = timeInMinutes,
            onFollowClicked = viewModel::onFollowClicked,
            onMediaClicked = onMediaClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme
        )
    }
}

@Composable
private fun AiringScreenContent(
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    airingSchedulesByDayOfWeekMap: Map<DayOfWeekLocal, List<AiringScheduleItem>>,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme
) {
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            airingSchedulesByDayOfWeekMap.entries.map {
                item {
                    Text(
                        text = it.key.name,
                        style = LocalTextStyles.current.category,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            .fillMaxWidth()
                    )
                }
                it.value.map {
                    item {
                        MediaItemCardCollapsed(
                            airingScheduleItem = it,
                            timeInMinutes = timeInMinutes,
                            onFollowClicked = onFollowClicked,
                            onMediaClicked = onMediaClicked,
                            preferredNamingScheme = preferredNamingScheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun AiringScreenPreview() {
    AiringScreenPreviewContent(
        airingSchedulesByDayOfWeekMap = MediaItemMapper.groupAiringSchedulesByDayOfWeek(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).toSortedMap()
    )
}

@Composable
@Preview
private fun AiringScreenMultipleInOneDayPreview() {
    AiringScreenPreviewContent(
        airingSchedulesByDayOfWeekMap = mapOf(
            DayOfWeekLocal.MONDAY to ModelTestDataCreator.baseAiringScheduleItemList()
        )
    )
}

@Composable
private fun AiringScreenPreviewContent(
    airingSchedulesByDayOfWeekMap: Map<DayOfWeekLocal, List<AiringScheduleItem>>
) {
    val timeInMinutes = 27785711L
    val swipeRefreshState = rememberSwipeRefreshState(false)

    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            swipeRefreshState = swipeRefreshState,
            onRefresh = { },
            airingSchedulesByDayOfWeekMap = airingSchedulesByDayOfWeekMap,
            timeInMinutes = timeInMinutes,
            onFollowClicked = { },
            onMediaClicked = { },
            preferredNamingScheme = NamingScheme.ENGLISH
        )
    }
}