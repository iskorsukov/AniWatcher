package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    settingsRepository: SettingsRepository
): ViewModel() {

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(
        MainActivityUiState(isRefreshing = false)
    )
    val uiState: StateFlow<MainActivityUiState> = _uiState

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
        _uiState.value = _uiState.value.copy(searchText = searchText)
    }

    fun onSortingOptionSelected(sortingOption: SortingOption) {
        _uiState.value = _uiState.value.copy(sortingOption = sortingOption)
    }
}