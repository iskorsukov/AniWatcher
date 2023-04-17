package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import javax.inject.Inject

interface ResetStateUiState: UiState {
    fun getDefault(): ResetStateUiState
}

sealed interface ResetStateInputEvent: AiringInputEvent, FollowingInputEvent,
    MainActivityInputEvent, MediaInputEvent
object ResetStateTriggeredInputEvent: ResetStateInputEvent

class ResetStateEventHandler<UiStateType: ResetStateUiState> @Inject constructor() {

    @Suppress("UNCHECKED_CAST") // cast is justified - being cast back to received type
    fun handleEvent(
        inputEvent: ResetStateInputEvent,
        originalUiStateType: UiStateType
    ): UiStateType {
        return when (inputEvent) {
            is ResetStateTriggeredInputEvent -> {
                originalUiStateType.getDefault()
            }
        } as UiStateType
    }
}