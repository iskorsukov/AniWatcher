package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.fab.ScrollToTopFab
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowClickedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterSelectionUpdatedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateTriggeredInputEvent
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AiringScreen(
    viewModel: AiringViewModel,
    isRefreshing: Boolean,
    settingsState: SettingsState,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit
) {
    val airingUiStateWithData by viewModel.uiStateWithDataFlow
        .collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var shouldShowFilterFormatDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        AiringScreenContent(
            lazyListState = lazyListState,
            airingUiState = airingUiStateWithData.uiState,
            airingSchedulesByDayOfWeekMap = airingUiStateWithData.schedulesByDayOfWeek,
            timeInMinutes = airingUiStateWithData.timeInMinutes,
            onFollowClicked = {
                viewModel.handleInputEvent(FollowClickedInputEvent(it))
            },
            onMediaClicked = onMediaClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onResetClicked = { viewModel.handleInputEvent(ResetStateTriggeredInputEvent) },
            onFilterFormatClicked = { shouldShowFilterFormatDialog = true }
        )
        ScrollToTopFab(
            lazyListState = lazyListState,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = LocalColors.current.onPrimary,
            contentColor = LocalColors.current.primary
        )
    }

    if (shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            deselectedFormatOptions = airingUiStateWithData.uiState.deselectedFormats,
            onDeselectedFormatsUpdated = { viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(it)) },
            onDismissRequest = { shouldShowFilterFormatDialog = false }
        )
    }
}

@Composable
private fun AiringScreenContent(
    lazyListState: LazyListState,
    airingUiState: AiringUiState,
    airingSchedulesByDayOfWeekMap: Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>>,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme,
    onFilterFormatClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            HeaderFlowRow(
                showReset = airingUiState.showReset,
                onResetClicked = onResetClicked
            ) {
                FilterFormatHeaderChip(deselectedFormats = airingUiState.deselectedFormats) {
                    onFilterFormatClicked.invoke()
                }
            }
        }
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
                        airingScheduleItem = it.first,
                        mediaItem = it.second,
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

@Composable
@Preview
private fun AiringScreenPreview() {
    AiringScreenPreviewContent(
        airingSchedulesByDayOfWeekMap = MediaItemMapper().groupAiringSchedulesByDayOfWeek(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            ),
            ModelTestDataCreator.TIME_IN_MINUTES
        ).toSortedMap()
    )
}

@Composable
@Preview
private fun AiringScreenMultipleInOneDayPreview() {
    AiringScreenPreviewContent(
        airingSchedulesByDayOfWeekMap = mapOf(
            DayOfWeekLocal.MONDAY to
                    listOf(ModelTestDataCreator.baseAiringScheduleItem() to ModelTestDataCreator.baseMediaItem)
        )
    )
}

@Composable
private fun AiringScreenPreviewContent(
    airingSchedulesByDayOfWeekMap: Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>>
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            lazyListState = lazyListState,
            airingUiState = AiringUiState.DEFAULT,
            airingSchedulesByDayOfWeekMap = airingSchedulesByDayOfWeekMap,
            timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
            onFollowClicked = { },
            onMediaClicked = { },
            preferredNamingScheme = NamingScheme.ENGLISH,
            onFilterFormatClicked = { },
            onResetClicked = { }
        )
    }
}