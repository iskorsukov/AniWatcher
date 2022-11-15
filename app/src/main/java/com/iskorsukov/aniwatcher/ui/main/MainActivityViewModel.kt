package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(
        MainActivityUiState(false)
    )
    val uiState: StateFlow<MainActivityUiState> = _uiState

    private val _searchTextState: MutableStateFlow<String> = MutableStateFlow("")
    val searchTextState: StateFlow<String> = _searchTextState

    private val _sortingOptionState: MutableStateFlow<SortingOption> = MutableStateFlow(SortingOption.AIRING_AT)
    val sortingOptionState: StateFlow<SortingOption> = _sortingOptionState

    fun loadAiringData() {
        _uiState.value = MainActivityUiState(true)
        val year = DateTimeHelper.currentYear(Calendar.getInstance())
        val season = DateTimeHelper.currentSeason(Calendar.getInstance())
        viewModelScope.launch {
            try {
                airingRepository.loadSeasonAiringData(year, season)
                _uiState.value = MainActivityUiState(false)
            } catch (e: Exception) {
                _uiState.value = MainActivityUiState(false, ErrorItem.LoadingData)
                return@launch
            }
        }
    }

    fun onSearchTextInput(searchText: String) {
        _searchTextState.value = searchText
    }

    fun onSortingOptionsIconClicked() {
        _uiState.value = _uiState.value.copy(showSortingOptionsDialog = true)
    }

    fun onSortingOptionSelected(sortingOption: SortingOption) {
        _sortingOptionState.value = sortingOption
    }

    fun onSortingOptionsDialogDismissed() {
        _uiState.value = _uiState.value.copy(showSortingOptionsDialog = false)
    }
}