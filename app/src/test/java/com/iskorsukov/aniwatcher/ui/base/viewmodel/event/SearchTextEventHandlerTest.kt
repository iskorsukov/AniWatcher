package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import org.junit.Test

class SearchTextEventHandlerTest {
    private val searchTextEventHandler: SearchTextEventHandler<MainActivityUiState> = SearchTextEventHandler()

    @Test
    fun handleEvent_searchTextChanged() {
        val searchText = "search"
        val uiState = searchTextEventHandler.handleEvent(
            SearchTextChangedInputEvent(searchText),
            MainActivityUiState.DEFAULT
        )
        assertThat(uiState.searchText).isEqualTo(searchText)
    }

    @Test
    fun handleEvent_appendSearchText() {
        val searchText = "search"
        var uiState = searchTextEventHandler.handleEvent(
            AppendSearchTextInputEvent(searchText),
            MainActivityUiState.DEFAULT
        )
        assertThat(uiState.searchText).isEqualTo(searchText)

        uiState = searchTextEventHandler.handleEvent(
            AppendSearchTextInputEvent(searchText),
            uiState
        )
        assertThat(uiState.searchText).isEqualTo("$searchText $searchText")
    }

    @Test
    fun handleEvent_searchFieldVisibilityChanged() {
        val uiState = searchTextEventHandler.handleEvent(
            SearchFieldVisibilityChangedInputEvent(true),
            MainActivityUiState.DEFAULT
        )
        assertThat(uiState.searchFieldOpen).isEqualTo(true)
    }

    @Test
    fun handleEvent_resetSearchText() {
        val uiState = searchTextEventHandler.handleEvent(
            ResetSearchTextInputEvent,
            MainActivityUiState.DEFAULT.copy(searchText = "search", searchFieldOpen = true)
        )
        assertThat(uiState).isEqualTo(MainActivityUiState.DEFAULT)
    }
}