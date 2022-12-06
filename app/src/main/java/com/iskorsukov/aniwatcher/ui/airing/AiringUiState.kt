package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

data class AiringUiState(
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean,
) {
    companion object {
        val DEFAULT = AiringUiState(emptyList(), null, false)
    }
}