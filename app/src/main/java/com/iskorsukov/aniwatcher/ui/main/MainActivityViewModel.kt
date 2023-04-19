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
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.MainActivityInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextEventHandler
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextInputEvent
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
    private val settingsRepository: SettingsRepository,
    notificationsRepository: NotificationsRepository,
    private val searchTextEventHandler: SearchTextEventHandler<MainActivityUiState>,
    private val seasonYearEventHandler: SeasonYearEventHandler,
    private val notificationsPermissionEventHandler: NotificationsPermissionEventHandler
) : ViewModel() {

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    private val _uiState: MutableStateFlow<MainActivityUiState> = MutableStateFlow(
        MainActivityUiState.DEFAULT
    )
    val uiState: StateFlow<MainActivityUiState> = _uiState
        .combine(notificationsRepository.unreadNotificationsCounterStateFlow) { uiState, unreadNotificationsCount ->
            uiState.copy(
                unreadNotificationsCount = unreadNotificationsCount
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, MainActivityUiState.DEFAULT)

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

    fun handleInputEvent(inputEvent: MainActivityInputEvent) {
        try {
            _uiState.value = when (inputEvent) {
                is SearchTextInputEvent -> searchTextEventHandler.handleEvent(
                    inputEvent,
                    _uiState.value
                )
                is SeasonYearInputEvent -> seasonYearEventHandler.handleEvent(
                    inputEvent,
                    _uiState.value,
                    settingsRepository
                )
                is NotificationsPermissionEvent -> notificationsPermissionEventHandler.handleEvent(
                    inputEvent,
                    _uiState.value,
                    settingsRepository
                )
                else -> throw IllegalArgumentException("Unsupported input event of type ${inputEvent::class.simpleName}")
            }
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onError(ErrorItem.ofThrowable(e))
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _uiState.value = _uiState.value.copy(errorItem = errorItem)
    }
}