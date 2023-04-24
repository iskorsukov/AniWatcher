package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.filterSearchMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.sortMediaFlow
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.format.rememberFilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.main.SearchFieldState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import com.iskorsukov.aniwatcher.ui.sorting.SortingOptionsDialogState
import com.iskorsukov.aniwatcher.ui.sorting.rememberSortingOptionsDialogState

@Composable
fun rememberFollowingScreenState(
    uiStateWithData: FollowingUiStateWithData,
    mediaItemMapper: MediaItemMapper,
    searchFieldState: SearchFieldState,
    sortingOptionsDialogState: SortingOptionsDialogState = rememberSortingOptionsDialogState(),
    filterFormatDialogState: FilterFormatDialogState = rememberFilterFormatDialogState()
): FollowingScreenState {
    return remember(
        uiStateWithData,
        mediaItemMapper,
        searchFieldState,
        sortingOptionsDialogState,
        filterFormatDialogState
    ) {
        FollowingScreenState(
            uiStateWithData = uiStateWithData,
            mediaItemMapper = mediaItemMapper,
            searchFieldState = searchFieldState,
            sortingOptionsDialogState = sortingOptionsDialogState,
            filterFormatDialogState = filterFormatDialogState
        )
    }
}

class FollowingScreenState(
    val uiStateWithData: FollowingUiStateWithData,
    val sortingOptionsDialogState: SortingOptionsDialogState,
    val filterFormatDialogState: FilterFormatDialogState,
    val searchFieldState: SearchFieldState,
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
                    uiStateWithData.mediaWithSchedulesMap,
                    uiStateWithData.timeInMinutes
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