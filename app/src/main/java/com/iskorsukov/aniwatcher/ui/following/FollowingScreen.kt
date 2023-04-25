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
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.header.SortingOptionHeaderChip
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder
import com.iskorsukov.aniwatcher.ui.base.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.main.state.SearchFieldState
import com.iskorsukov.aniwatcher.ui.main.state.rememberSearchFieldState
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import com.iskorsukov.aniwatcher.ui.base.sorting.SelectSortingOptionDialog

@Composable
fun FollowingScreen(
    viewModel: FollowingViewModel,
    mediaItemMapper: MediaItemMapper,
    searchFieldState: SearchFieldState,
    settingsState: SettingsState,
    onMediaClicked: (MediaItem) -> Unit
) {
    val followingScreenData by viewModel.dataFlow
        .collectAsStateWithLifecycle()
    val followingScreenState = rememberFollowingScreenState(
        uiStateWithData = followingScreenData,
        mediaItemMapper = mediaItemMapper,
        searchFieldState = searchFieldState
    )

    val listState = rememberLazyListState()

    LaunchedEffect(followingScreenState.searchFieldState.searchText) {
        listState.scrollToItem(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FollowingScreenContent(
            followingScreenState = followingScreenState,
            onFollowClicked = { viewModel.onFollowMedia(it) },
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onMediaClicked = onMediaClicked,
            listState = listState,
        )
    }

    if (followingScreenState.sortingOptionsDialogState.shouldShowSortingOptionsDialog) {
        SelectSortingOptionDialog(
            sortingOptionsDialogState = followingScreenState.sortingOptionsDialogState
        )
    }
    if (followingScreenState.filterFormatDialogState.shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            filterFormatDialogState = followingScreenState.filterFormatDialogState
        )
    }
}

@Composable
fun FollowingScreenContent(
    followingScreenState: FollowingScreenState,
    onFollowClicked: ((MediaItem) -> Unit),
    preferredNamingScheme: NamingScheme,
    onMediaClicked: (MediaItem) -> Unit,
    listState: LazyListState,
) {
    if (
        followingScreenState.searchFieldState.searchText.isBlank() &&
        followingScreenState.mediaWithNextAiringMap.isEmpty()
    ) {
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
                    showReset = followingScreenState.shouldShowResetButton,
                    onResetClicked = followingScreenState::reset
                ) {
                    SortingOptionHeaderChip(
                        selectedSortingOption = followingScreenState.sortingOptionsDialogState.selectedOption
                    ) {
                        followingScreenState.sortingOptionsDialogState.show()
                    }
                    FilterFormatHeaderChip(
                        deselectedFormats = followingScreenState.filterFormatDialogState.deselectedFormats
                    ) {
                        followingScreenState.filterFormatDialogState.show()
                    }
                }
            }
            followingScreenState.mediaWithNextAiringMap.entries.forEach {
                item {
                    MediaItemCardExtended(
                        mediaItem = it.key,
                        airingScheduleItem = it.value,
                        timeInMinutes = followingScreenState.uiStateWithData.timeInMinutes,
                        onFollowClicked = onFollowClicked,
                        onMediaClicked = onMediaClicked,
                        onGenreChipClicked = followingScreenState.searchFieldState::appendText,
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
    val followingScreenState = rememberFollowingScreenState(
        uiStateWithData = FollowingScreenData(
            mediaWithSchedulesMap = emptyMap(),
            timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
        ),
        mediaItemMapper = MediaItemMapper(),
        searchFieldState = rememberSearchFieldState()
    )
    FollowingScreenContent(
        followingScreenState = followingScreenState,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        listState = rememberLazyListState(),
    )
}

@Composable
@Preview
private fun FollowingScreenPreview() {
    val followingScreenState = rememberFollowingScreenState(
        uiStateWithData = FollowingScreenData(
            mediaWithSchedulesMap = mapOf(
                ModelTestDataCreator.baseMediaItem.isFollowing(true) to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            ),
            timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
        ),
        mediaItemMapper = MediaItemMapper(),
        searchFieldState = rememberSearchFieldState()
    )
    FollowingScreenContent(
        followingScreenState = followingScreenState,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        listState = rememberLazyListState()
    )
}