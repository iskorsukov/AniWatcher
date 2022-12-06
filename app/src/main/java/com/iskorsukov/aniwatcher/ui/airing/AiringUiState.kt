package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.model.MediaItem

data class AiringUiState(
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val showReset: Boolean
) {
    companion object {
        val DEFAULT = AiringUiState(emptyList(), false)
    }
}