package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class MediaUiState(
    val sortingOption: SortingOption,
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean
) {
    companion object {
        val DEFAULT = MediaUiState(SortingOption.AIRING_AT, emptyList(), null, false)
    }
}