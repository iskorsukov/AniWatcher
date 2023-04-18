package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SortingOptionsUiState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import java.util.concurrent.TimeUnit

data class MediaUiState(
    val mediaWithNextAiringMap: Map<MediaItem, AiringScheduleItem?>,
    val timeInMinutes: Long,
    val sortingOption: SortingOption,
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean,
    override val searchText: String
): SearchTextUiState, SortingOptionsUiState, FormatsFilterUiState, ResetStateUiState {
    companion object {
        val DEFAULT = MediaUiState(
            mediaWithNextAiringMap = emptyMap(),
            timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()),
            sortingOption = SortingOption.AIRING_AT,
            deselectedFormats = emptyList(),
            errorItem = null,
            showReset = false,
            searchText = ""
        )
    }

    override fun copyWithSearchTextStateUpdated(
        searchText: String?,
        isSearchFieldOpen: Boolean?
    ): SearchTextUiState {
        return this.copy(
            searchText = searchText ?: this.searchText,
        )
    }

    override fun copyWithSortingOption(sortingOption: SortingOption): SortingOptionsUiState {
        return this.copy(sortingOption = sortingOption)
    }

    override fun copyWithDeselectedFormats(deselectedFormats: List<MediaItem.LocalFormat>): FormatsFilterUiState {
        return this.copy(deselectedFormats = deselectedFormats)
    }

    override fun getDefault(): ResetStateUiState {
        return DEFAULT
    }
}