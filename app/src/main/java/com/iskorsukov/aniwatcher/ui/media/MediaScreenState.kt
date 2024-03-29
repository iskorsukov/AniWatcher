package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.filterSearchMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.sortMediaFlow
import com.iskorsukov.aniwatcher.ui.base.format.FilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.base.format.rememberFilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.main.state.SearchFieldState
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOptionsDialogState
import com.iskorsukov.aniwatcher.ui.base.sorting.rememberSortingOptionsDialogState
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberMediaScreenState(
    uiState: MediaScreenData,
    searchFieldState: SearchFieldState,
    mediaItemMapper: MediaItemMapper,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
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
            searchFieldState = searchFieldState,
            filterFormatDialogState = filterFormatDialogState,
            sortingOptionsDialogState = sortingOptionsDialogState,
            mediaItemMapper = mediaItemMapper
        )
    }
}

class MediaScreenState(
    val coroutineScope: CoroutineScope,
    val uiState: MediaScreenData,
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

data class MediaScreenData(
    val mediaWithSchedulesMap: Map<MediaItem, List<AiringScheduleItem>> = emptyMap(),
    val timeInMinutes: Long = 0L,
    val errorItem: ErrorItem? = null
)