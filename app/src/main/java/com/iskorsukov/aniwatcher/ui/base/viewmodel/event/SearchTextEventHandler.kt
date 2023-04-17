package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import javax.inject.Inject

interface SearchTextUiState: UiState {
    val searchText: String
    fun copyWithSearchTextStateUpdated(
        searchText: String? = null,
        isSearchFieldOpen: Boolean? = null
    ): SearchTextUiState
}

sealed interface SearchTextInputEvent: FollowingInputEvent, MainActivityInputEvent, MediaInputEvent
data class SearchTextChangedInputEvent(val searchText: String): SearchTextInputEvent
data class AppendSearchTextInputEvent(val searchText: String): SearchTextInputEvent
data class SearchFieldVisibilityChangedInputEvent(val isSearchFieldVisible: Boolean):
    SearchTextInputEvent
object ResetSearchTextInputEvent: SearchTextInputEvent

class SearchTextEventHandler<UiStateType: SearchTextUiState> @Inject constructor() {

    @Suppress("UNCHECKED_CAST") // cast is justified - being cast back to received type
    fun handleEvent(
        inputEvent: SearchTextInputEvent,
        originalUiState: UiStateType
    ): UiStateType {
        return when (inputEvent) {
            is SearchTextChangedInputEvent -> {
                originalUiState.copyWithSearchTextStateUpdated(inputEvent.searchText)
            }
            is AppendSearchTextInputEvent -> {
                val currentSearchText = originalUiState.searchText
                val appendedSearchText = inputEvent.searchText
                if (currentSearchText.isBlank()) {
                    originalUiState.copyWithSearchTextStateUpdated(appendedSearchText)
                } else {
                    originalUiState.copyWithSearchTextStateUpdated("$currentSearchText $appendedSearchText")
                }
            }
            is SearchFieldVisibilityChangedInputEvent -> {
                originalUiState.copyWithSearchTextStateUpdated(
                    isSearchFieldOpen = inputEvent.isSearchFieldVisible
                )
            }
            is ResetSearchTextInputEvent -> {
                originalUiState.copyWithSearchTextStateUpdated(
                    searchText = "",
                    isSearchFieldOpen = false
                )
            }
        } as UiStateType
    }
}