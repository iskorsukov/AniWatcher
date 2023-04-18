package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.fab.ScrollToTopFab
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.header.SortingOptionHeaderChip
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowClickedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterSelectionUpdatedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateTriggeredInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextChangedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SortingOptionChangedInputEvent
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaScreen(
    viewModel: MediaViewModel,
    uiState: MainActivityUiState,
    settingsState: SettingsState,
    timeInMinutes: Long,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit,
    onGenreChipClicked: (String) -> Unit
) {
    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val mediaUiState by viewModel.uiStateFlow
        .collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(uiState.isRefreshing, onRefresh)

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var shouldShowSortingOptionsDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var shouldShowFilterFormatDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState.searchText) {
        viewModel.handleInputEvent(SearchTextChangedInputEvent(uiState.searchText))
        listState.scrollToItem(0)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        MediaScreenContent(
            mediaItemWithNextAiringMap = mediaFlow,
            listState = listState,
            timeInMinutes = timeInMinutes,
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onFollowClicked = { viewModel.handleInputEvent(FollowClickedInputEvent(it)) },
            onGenreChipClicked = onGenreChipClicked,
            onMediaClicked = onMediaClicked,
            onSelectSortingOptionClicked = { shouldShowSortingOptionsDialog = true },
            mediaUiState = mediaUiState,
            onFilterFormatClicked = { shouldShowFilterFormatDialog = true },
            onResetClicked = { viewModel.handleInputEvent(ResetStateTriggeredInputEvent) }
        )
        ScrollToTopFab(
            lazyListState = listState,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = LocalColors.current.onPrimary,
            contentColor = LocalColors.current.primary
        )
    }

    if (shouldShowSortingOptionsDialog) {
        SelectSortingOptionDialog(
            onSortingOptionSelected = { viewModel.handleInputEvent(SortingOptionChangedInputEvent(it)) },
            onDismissRequest = { shouldShowSortingOptionsDialog = false },
            selectedOption = mediaUiState.sortingOption
        )
    }

    if (shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            mediaUiState.deselectedFormats
        ) { formats ->
            shouldShowFilterFormatDialog = false
            viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(formats))
        }
    }
}

@Composable
private fun MediaScreenContent(
    mediaItemWithNextAiringMap: Map<MediaItem, AiringScheduleItem?>,
    listState: LazyListState,
    timeInMinutes: Long,
    preferredNamingScheme: NamingScheme,
    mediaUiState: MediaUiState,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit,
    onSelectSortingOptionClicked: () -> Unit,
    onFilterFormatClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item {
            HeaderFlowRow(
                showReset = mediaUiState.showReset,
                onResetClicked = onResetClicked
            ) {
                SortingOptionHeaderChip(selectedSortingOption = mediaUiState.sortingOption) {
                    onSelectSortingOptionClicked.invoke()
                }
                FilterFormatHeaderChip(deselectedFormats = mediaUiState.deselectedFormats) {
                    onFilterFormatClicked.invoke()
                }
            }
        }
        mediaItemWithNextAiringMap.entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = timeInMinutes,
                    onFollowClicked = onFollowClicked,
                    onMediaClicked = onMediaClicked,
                    onGenreChipClicked = onGenreChipClicked,
                    preferredNamingScheme = preferredNamingScheme
                )
            }
        }
    }
}

@Composable
@Preview
fun MediaScreenPreview() {
    MediaScreenContent(
        mediaItemWithNextAiringMap = MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ),
        listState = rememberLazyListState(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH,
        onFollowClicked = { },
        onMediaClicked = { },
        onGenreChipClicked = { },
        onSelectSortingOptionClicked = { },
        mediaUiState = MediaUiState.DEFAULT,
        onFilterFormatClicked = { },
        onResetClicked = { }
    )
}