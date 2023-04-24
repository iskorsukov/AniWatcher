package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.filterSearchMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.sortMediaFlow
import com.iskorsukov.aniwatcher.ui.main.SearchFieldState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberMediaScreenState(
    uiState: MediaUiStateWithData,
    searchFieldState: SearchFieldState,
    mediaItemMapper: MediaItemMapper,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState(),
    filterFormatDialogState: FilterFormatDialogState = rememberFilterFormatDialogState(),
    sortingOptionsDialogState: SortingOptionsDialogState = rememberSortingOptionsDialogState()
): MediaScreenState {
    return remember(
        uiState,
        searchFieldState,
        mediaItemMapper,
        coroutineScope,
        filterFormatDialogState,
        sortingOptionsDialogState
    ) {
        MediaScreenState(
            coroutineScope = coroutineScope,
            uiState = uiState,
            listState = listState,
            searchFieldState = searchFieldState,
            filterFormatDialogState = filterFormatDialogState,
            sortingOptionsDialogState = sortingOptionsDialogState,
            mediaItemMapper = mediaItemMapper
        )
    }
}

class MediaScreenState(
    val coroutineScope: CoroutineScope,
    val uiState: MediaUiStateWithData,
    val listState: LazyListState,
    val searchFieldState: SearchFieldState,
    val filterFormatDialogState: FilterFormatDialogState,
    val sortingOptionsDialogState: SortingOptionsDialogState,
    val mediaItemMapper: MediaItemMapper
) {
    val shouldShowResetButton: Boolean
        get() {
            val deselectedFormatsNotDefault =
                filterFormatDialogState.deselectedFormats.isNotEmpty()
            val sortingOptionNotDefault =
                sortingOptionsDialogState.selectedOption != SortingOption.AIRING_AT
            return deselectedFormatsNotDefault || sortingOptionNotDefault
        }

    val mediaWithNextAiringMap: Map<MediaItem, AiringScheduleItem?>
        get() {
            return mediaItemMapper
                .groupMediaWithNextAiringSchedule(
                    uiState.mediaWithSchedulesMap,
                    uiState.timeInMinutes
                ).let {
                    filterSearchMediaFlow(
                        it, searchFieldState.searchText
                    )
                }.let {
                    filterFormatMediaFlow(
                        it, filterFormatDialogState.deselectedFormats
                    )
                }.let {
                    sortMediaFlow(
                        it, sortingOptionsDialogState.selectedOption
                    )
                }
        }

    fun reset() {
        filterFormatDialogState.deselectedFormats.clear()
        sortingOptionsDialogState.selectedOption = SortingOption.AIRING_AT
    }
}