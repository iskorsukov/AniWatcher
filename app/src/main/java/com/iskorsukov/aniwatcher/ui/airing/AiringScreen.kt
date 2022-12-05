package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.fab.ScrollToTopFab
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AiringScreen(
    viewModel: AiringViewModel,
    uiState: MainActivityUiState,
    settingsState: SettingsState,
    timeInMinutes: Long,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit
) {
    val airingScheduleItemList by viewModel
        .airingSchedulesByDayOfWeekFlow.collectAsStateWithLifecycle(initialValue = emptyMap())

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            swipeRefreshState = swipeRefreshState,
            lazyListState = lazyListState,
            onRefresh = onRefresh,
            airingSchedulesByDayOfWeekMap = MediaItemMapper.filterExtraFollowedAiringSchedules(
                airingScheduleItemList, settingsState, uiState.seasonYear
            ),
            timeInMinutes = timeInMinutes,
            onFollowClicked = viewModel::onFollowClicked,
            onMediaClicked = onMediaClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme
        )
        ScrollToTopFab(
            lazyListState = lazyListState,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun AiringScreenContent(
    swipeRefreshState: SwipeRefreshState,
    lazyListState: LazyListState,
    onRefresh: () -> Unit,
    airingSchedulesByDayOfWeekMap: Map<DayOfWeekLocal, List<AiringScheduleItem>>,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        indicator = { state, triggerDistance ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = triggerDistance,
                backgroundColor = LocalColors.current.onPrimary,
                contentColor = LocalColors.current.primary
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
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
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            swipeRefreshState = swipeRefreshState,
            lazyListState = lazyListState,
            onRefresh = { },
            airingSchedulesByDayOfWeekMap = airingSchedulesByDayOfWeekMap,
            timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
            onFollowClicked = { },
            onMediaClicked = { },
            preferredNamingScheme = NamingScheme.ENGLISH
        )
    }
}