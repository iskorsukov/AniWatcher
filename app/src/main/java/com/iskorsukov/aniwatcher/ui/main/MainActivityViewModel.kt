package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.onboarding.OnboardingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val settingsRepository: SettingsRepository,
    notificationsRepository: NotificationsRepository
): OnboardingViewModel(settingsRepository) {

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow
    val unreadNotificationsState: StateFlow<Int> = notificationsRepository.unreadNotificationsCounterStateFlow

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(
        MainActivityUiState(isRefreshing = false)
    )
    val uiState: StateFlow<MainActivityUiState> = _uiState

    fun loadAiringData() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        onError(null)
        viewModelScope.launch {
            try {
                if (settingsState.value.scheduleType == ScheduleType.ALL) {
                    val weekStartEndSeconds = DateTimeHelper.currentWeekStartToEndSeconds(Calendar.getInstance())
                    airingRepository.loadRangeAiringData(weekStartEndSeconds.first, weekStartEndSeconds.second)
                } else {
                    val seasonYear = settingsState.value.selectedSeasonYear
                    airingRepository.loadSeasonAiringData(seasonYear.year, seasonYear.season.name)
                }
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(throwable)
                _uiState.value = _uiState.value.copy(isRefreshing = false)
                onError(ErrorItem.ofThrowable(throwable))
            }
        }
    }

    override fun onError(errorItem: ErrorItem?) {
        _uiState.value = _uiState.value.copy(errorItem = errorItem)
    }

    fun onSearchTextInput(searchText: String) {
        _uiState.value = _uiState.value.copy(searchText = searchText)
    }

    fun appendSearchText(searchText: String) {
        val currentSearchText = _uiState.value.searchText
        if (currentSearchText.isBlank()) {
            onSearchTextInput(searchText)
        } else {
            onSearchTextInput("$currentSearchText $searchText")
        }
    }

    fun onSearchFieldOpenChange(isSearchFieldOpen: Boolean) {
        _uiState.value = _uiState.value.copy(searchFieldOpen = isSearchFieldOpen)
    }

    fun onSeasonYearSelected(seasonYear: DateTimeHelper.SeasonYear) {
        settingsRepository.setSelectedSeasonYear(seasonYear)
    }

    fun resetTopBarState() {
        _uiState.value = _uiState.value.copy(
            searchText = "",
            searchFieldOpen = false
        )
    }

    fun onNotificationsPermissionMissing() {
        settingsRepository.setNotificationsEnabled(false)
        _uiState.value = _uiState.value.copy(
            showNotificationsPermissionRationale = true
        )
    }

    fun onNotificationsPermissionGrantClicked() {
        _uiState.value = _uiState.value.copy(
            showNotificationsPermissionRationale = false,
            launchNotificationPermissionRequest = true
        )
    }

    fun onNotificationsPermissionDisableClicked() {
        _uiState.value = _uiState.value.copy(
            showNotificationsPermissionRationale = false
        )
        settingsRepository.setNotificationsEnabled(false)
    }

    fun onNotificationsPermissionGranted() {
        settingsRepository.setNotificationsEnabled(true)
        _uiState.value = _uiState.value.copy(
            notificationsPermissionGranted = true,
            launchNotificationPermissionRequest = false
        )
    }

    fun onNotificationsPermissionDenied() {
        settingsRepository.setNotificationsEnabled(false)
        _uiState.value = _uiState.value.copy(
            notificationsPermissionGranted = false,
            launchNotificationPermissionRequest = false
        )
    }
}