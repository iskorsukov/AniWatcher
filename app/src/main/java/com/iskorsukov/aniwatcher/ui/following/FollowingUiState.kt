package com.iskorsukov.aniwatcher.ui.following

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class FollowingUiState(
    val sortingOption: SortingOption,
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean = false
) {
    companion object {
        val DEFAULT = FollowingUiState(SortingOption.AIRING_AT, emptyList(), null)
    }
}