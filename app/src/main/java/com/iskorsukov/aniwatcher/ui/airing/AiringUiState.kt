package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateUiState

data class AiringUiState(
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean,
): FormatsFilterUiState, ResetStateUiState {

    companion object {
        val DEFAULT = AiringUiState(emptyList(), null, false)
    }

    override fun copyWithDeselectedFormats(deselectedFormats: List<MediaItem.LocalFormat>): FormatsFilterUiState {
        return this.copy(deselectedFormats = deselectedFormats)
    }

    override fun getDefault(): ResetStateUiState {
        return DEFAULT
    }
}