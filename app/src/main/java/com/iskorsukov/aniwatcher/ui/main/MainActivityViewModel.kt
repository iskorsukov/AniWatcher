package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.BuildConfig
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.main.state.MainScreenData
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

    private val _dataFlow: MutableStateFlow<MainScreenData> = MutableStateFlow(
        MainScreenData()
    )
    val dataFlow: StateFlow<MainScreenData> = _dataFlow
        .combine(notificationsRepository.unreadNotificationsCounterStateFlow) { data, unreadNotificationsCount ->
            data.copy(
                unreadNotificationsCount = unreadNotificationsCount
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily, MainScreenData()
        )

    fun loadAiringData() {
        _dataFlow.value = _dataFlow.value.copy(isRefreshing = true)
        onError(null)
        viewModelScope.launch {
            try {
                if (settingsState.value.isThisWeekSelected()) {
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
            } catch (throwable: Throwable) {
                if (!BuildConfig.DEBUG) {
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }
                throwable.printStackTrace()
                onError(ErrorItem.ofThrowable(throwable))
            } finally {
                _dataFlow.value = _dataFlow.value.copy(isRefreshing = false)
            }
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _dataFlow.value = _dataFlow.value.copy(errorItem = errorItem)
    }
}