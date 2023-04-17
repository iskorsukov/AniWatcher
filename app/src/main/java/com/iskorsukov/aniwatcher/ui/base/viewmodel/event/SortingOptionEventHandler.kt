package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import javax.inject.Inject

interface SortingOptionsUiState: UiState {
    fun copyWithSortingOption(sortingOption: SortingOption): SortingOptionsUiState
}

sealed interface SortingOptionInputEvent: FollowingInputEvent, MediaInputEvent
data class SortingOptionChangedInputEvent(val sortingOption: SortingOption): SortingOptionInputEvent

class SortingOptionEventHandler<UiStateType: SortingOptionsUiState> @Inject constructor() {

    @Suppress("UNCHECKED_CAST") // cast is justified - being cast back to received type
    fun handleEvent(
        inputEvent: SortingOptionInputEvent,
        originalUiState: UiStateType
    ): UiStateType {
        return when (inputEvent) {
            is SortingOptionChangedInputEvent -> {
                originalUiState.copyWithSortingOption(inputEvent.sortingOption)
            }
        } as UiStateType
    }
}