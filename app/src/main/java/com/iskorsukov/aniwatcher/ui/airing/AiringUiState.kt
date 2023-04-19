package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateUiState
import java.util.concurrent.TimeUnit

data class AiringUiStateWithData(
    val schedulesByDayOfWeek: Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>> = emptyMap(),
    val timeInMinutes: Long = 0L,
    val uiState: AiringUiState = AiringUiState.DEFAULT
)

data class AiringUiState(
    val deselectedFormats: List<MediaItem.LocalFormat>,
    val errorItem: ErrorItem?,
    val showReset: Boolean,
): FormatsFilterUiState, ResetStateUiState {

    companion object {
        val DEFAULT = AiringUiState(
            deselectedFormats = emptyList(),
            errorItem = null,
            showReset = false
        )
    }

    override fun copyWithDeselectedFormats(deselectedFormats: List<MediaItem.LocalFormat>): FormatsFilterUiState {
        return this.copy(deselectedFormats = deselectedFormats)
    }

    override fun getDefault(): ResetStateUiState {
        return DEFAULT
    }
}