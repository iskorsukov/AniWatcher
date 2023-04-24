package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.fab.ScrollToTopFab
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.header.SortingOptionHeaderChip
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.main.SearchFieldState
import com.iskorsukov.aniwatcher.ui.main.rememberSearchFieldState
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaScreen(
    viewModel: MediaViewModel,
    mediaItemMapper: MediaItemMapper,
    isRefreshing: Boolean,
    searchFieldState: SearchFieldState,
    preferredNamingScheme: NamingScheme,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit
) {
    val mediaUiStateWithData by viewModel.uiStateWithDataFlow
        .collectAsStateWithLifecycle()
    val mediaScreenState = rememberMediaScreenState(
        uiState = mediaUiStateWithData,
        searchFieldState = searchFieldState,
        mediaItemMapper = mediaItemMapper
    )

    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    val shouldShowSortingOptionsDialog = mediaScreenState
        .sortingOptionsDialogState
        .shouldShowSortingOptionsDialog
    val shouldShowFilterFormatDialog = mediaScreenState
        .filterFormatDialogState
        .shouldShowFilterFormatDialog

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        MediaScreenContent(
            mediaScreenState = mediaScreenState,
            preferredNamingScheme = preferredNamingScheme,
            onFollowClicked = { viewModel.onFollowMedia(it) },
            onMediaClicked = onMediaClicked,
        )
        ScrollToTopFab(
            lazyListState = mediaScreenState.listState,
            coroutineScope = mediaScreenState.coroutineScope,
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

    if (shouldShowSortingOptionsDialog) {
        SelectSortingOptionDialog(
            mediaScreenState.sortingOptionsDialogState
        )
    }

    if (shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            mediaScreenState.filterFormatDialogState
        )
    }
}

@Composable
private fun MediaScreenContent(
    mediaScreenState: MediaScreenState,
    preferredNamingScheme: NamingScheme,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = mediaScreenState.listState
    ) {
        item {
            HeaderFlowRow(
                showReset = mediaScreenState.shouldShowResetButton,
                onResetClicked = mediaScreenState::reset
            ) {
                SortingOptionHeaderChip(
                    selectedSortingOption = mediaScreenState.sortingOptionsDialogState.selectedOption
                ) {
                    mediaScreenState.sortingOptionsDialogState.show()
                }
                FilterFormatHeaderChip(
                    deselectedFormats = mediaScreenState.filterFormatDialogState.deselectedFormats
                ) {
                    mediaScreenState.filterFormatDialogState.show()
                }
            }
        }
        mediaScreenState.mediaWithNextAiringMap.entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = mediaScreenState.uiState.timeInMinutes,
                    onFollowClicked = onFollowClicked,
                    onMediaClicked = onMediaClicked,
                    onGenreChipClicked = mediaScreenState.searchFieldState::appendText,
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
        mediaScreenState = rememberMediaScreenState(
            uiState = MediaUiStateWithData(
                mapOf(
                    ModelTestDataCreator.baseMediaItem to
                            ModelTestDataCreator.baseAiringScheduleItemList()
                ),
                ModelTestDataCreator.TIME_IN_MINUTES
            ),
            searchFieldState = rememberSearchFieldState(),
            mediaItemMapper = MediaItemMapper()
        ),
        preferredNamingScheme = NamingScheme.ENGLISH,
        onFollowClicked = { },
        onMediaClicked = { }
    )
}