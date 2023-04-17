package com.iskorsukov.aniwatcher.ui.details

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.uistate.UiState

data class DetailsUiState(
    val mediaItemWithSchedules: Pair<MediaItem, List<AiringScheduleItem>>?,
    val errorItem: ErrorItem?
): UiState {
    companion object {
        val DEFAULT = DetailsUiState(null, null)
    }
}