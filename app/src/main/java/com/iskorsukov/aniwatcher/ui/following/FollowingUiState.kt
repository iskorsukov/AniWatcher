package com.iskorsukov.aniwatcher.ui.following

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SortingOptionsUiState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class FollowingUiState(
    val sortingOption: SortingOption,
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean = false,
    override val searchText: String = ""
): SearchTextUiState, SortingOptionsUiState, FormatsFilterUiState, ResetStateUiState {
    companion object {
        val DEFAULT = FollowingUiState(SortingOption.AIRING_AT, emptyList(), null)
    }

    override fun copyWithSearchTextStateUpdated(
        searchText: String?,
        isSearchFieldOpen: Boolean?
    ): FollowingUiState {
        return this.copy(
            searchText = searchText ?: this.searchText,
        )
    }

    override fun copyWithSortingOption(sortingOption: SortingOption): FollowingUiState {
        return this.copy(sortingOption = sortingOption)
    }

    override fun copyWithDeselectedFormats(deselectedFormats: List<MediaItem.LocalFormat>): FollowingUiState {
        return this.copy(deselectedFormats = deselectedFormats)
    }

    override fun getDefault(): FollowingUiState {
        return DEFAULT
    }
}