package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import javax.inject.Inject

interface FormatsFilterUiState: UiState {
    fun copyWithDeselectedFormats(deselectedFormats: List<MediaItem.LocalFormat>): FormatsFilterUiState
}

sealed interface FormatsFilterInputEvent: AiringInputEvent, FollowingInputEvent, MediaInputEvent
data class FormatsFilterSelectionUpdatedInputEvent(val deselectedFormats: List<MediaItem.LocalFormat>):
    FormatsFilterInputEvent

class FormatsFilterEventHandler<UiStateType: FormatsFilterUiState> @Inject constructor() {

    @Suppress("UNCHECKED_CAST") // cast is justified - being cast back to received type
    fun handleEvent(
        inputEvent: FormatsFilterInputEvent,
        originalUiState: UiStateType
    ): UiStateType {
        return when (inputEvent) {
            is FormatsFilterSelectionUpdatedInputEvent -> {
                originalUiState.copyWithDeselectedFormats(inputEvent.deselectedFormats)
            }
        } as UiStateType
    }
}