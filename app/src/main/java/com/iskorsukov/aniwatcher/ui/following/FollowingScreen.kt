package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.header.SortingOptionHeaderChip
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowClickedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateTriggeredInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextChangedInputEvent
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.format.rememberFilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.sorting.rememberSortingOptionsDialogState

@Composable
fun FollowingScreen(
    viewModel: FollowingViewModel,
    searchText: String,
    settingsState: SettingsState,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit
) {
    val followingUiStateWithData by viewModel.uiStateWithDataFlow
        .collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    LaunchedEffect(searchText) {
        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))
        listState.scrollToItem(0)
    }

    val filterFormatDialogState = rememberFilterFormatDialogState()
    val sortingOptionsDialogState = rememberSortingOptionsDialogState()

    Box(modifier = Modifier.fillMaxSize()) {
        FollowingScreenContent(
            followingMediaMap = followingUiStateWithData.mediaWithNextAiringMap,
            searchTextIsEmpty = searchText.trim().length < 4,
            timeInMinutes = followingUiStateWithData.timeInMinutes,
            onFollowClicked = { viewModel.handleInputEvent(FollowClickedInputEvent(it)) },
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onMediaClicked = onMediaClicked,
            onGenreChipClicked = onGenreChipClicked,
            listState = listState,
            followingUiState = followingUiStateWithData.uiState,
            onSelectSortingOptionClicked = { sortingOptionsDialogState.show() },
            onFilterFormatClicked = { filterFormatDialogState.show() },
            onResetClicked = { viewModel.handleInputEvent(ResetStateTriggeredInputEvent) }
        )
    }

    if (sortingOptionsDialogState.shouldShowSortingOptionsDialog) {
        SelectSortingOptionDialog(
            sortingOptionsDialogState = sortingOptionsDialogState
        )
    }
    if (filterFormatDialogState.shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            filterFormatDialogState = filterFormatDialogState
        )
    }
}

@Composable
fun FollowingScreenContent(
    followingMediaMap: Map<MediaItem, AiringScheduleItem?>,
    searchTextIsEmpty: Boolean,
    timeInMinutes: Long,
    onFollowClicked: ((MediaItem) -> Unit),
    preferredNamingScheme: NamingScheme,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit,
    listState: LazyListState,
    followingUiState: FollowingUiState,
    onSelectSortingOptionClicked: () -> Unit,
    onFilterFormatClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    if (followingMediaMap.isEmpty() && searchTextIsEmpty) {
        EmptyDataPlaceholder(
            iconResId = R.drawable.ic_baseline_add_circle_outline_24_gray,
            labelResId = R.string.following_data_empty_label,
            subLabelResId = R.string.following_data_empty_sub_label,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            item {
                HeaderFlowRow(
                    showReset = followingUiState.showReset,
                    onResetClicked = onResetClicked
                ) {
                    SortingOptionHeaderChip(selectedSortingOption = followingUiState.sortingOption) {
                        onSelectSortingOptionClicked.invoke()
                    }
                    FilterFormatHeaderChip(deselectedFormats = followingUiState.deselectedFormats) {
                        onFilterFormatClicked.invoke()
                    }
                }
            }
            followingMediaMap.entries.forEach {
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
}

@Composable
@Preview
private fun FollowingScreenEmptyPreview() {
    FollowingScreenContent(
        followingMediaMap = emptyMap(),
        searchTextIsEmpty = true,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState(),
        followingUiState = FollowingUiState.DEFAULT,
        onSelectSortingOptionClicked = { },
        onFilterFormatClicked = { },
        onResetClicked = { }
    )
}

@Composable
@Preview
private fun FollowingScreenPreview() {
    val followedMediaItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
    FollowingScreenContent(
        followingMediaMap = MediaItemMapper().groupMediaWithNextAiringSchedule(
            mapOf(
                 followedMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            ),
            ModelTestDataCreator.TIME_IN_MINUTES
        ).filterKeys { it.isFollowing },
        searchTextIsEmpty = true,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState(),
        followingUiState = FollowingUiState.DEFAULT,
        onSelectSortingOptionClicked = { },
        onFilterFormatClicked = { },
        onResetClicked = { }
    )
}