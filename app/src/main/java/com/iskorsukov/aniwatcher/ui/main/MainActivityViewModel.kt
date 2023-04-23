package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    settingsRepository: SettingsRepository,
    notificationsRepository: NotificationsRepository
) : ViewModel() {

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(
        MainActivityUiState()
    )
    val uiState: StateFlow<MainActivityUiState> = _uiState
        .combine(notificationsRepository.unreadNotificationsCounterStateFlow) { uiState, unreadNotificationsCount ->
            uiState.copy(
                unreadNotificationsCount = unreadNotificationsCount
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, MainActivityUiState())

    fun loadAiringData() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        onError(null)
        viewModelScope.launch {
            try {
                if (settingsState.value.selectedSeasonYear == DateTimeHelper.SeasonYear.THIS_WEEK) {
                    val weekStartEndSeconds =
                        DateTimeHelper.currentWeekStartToEndSeconds(Calendar.getInstance())
                    airingRepository.loadRangeAiringData(
                        weekStartEndSeconds.first,
                        weekStartEndSeconds.second
                    )
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

    private fun onError(errorItem: ErrorItem?) {
        _uiState.value = _uiState.value.copy(errorItem = errorItem)
    }
}