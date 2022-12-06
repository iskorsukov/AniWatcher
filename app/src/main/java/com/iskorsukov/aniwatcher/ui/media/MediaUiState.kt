package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class MediaUiState(
    val sortingOption: SortingOption,
    val deselectedFormats: List<MediaItem.LocalFormat>
) {
    companion object {
        val DEFAULT = MediaUiState(SortingOption.AIRING_AT, emptyList())
    }
}