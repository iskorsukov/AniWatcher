package com.iskorsukov.aniwatcher.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsInteractor
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    airingRepository: AiringRepository,
    private val notificationsRepository: NotificationsRepository,
    private val notificationsInteractor: NotificationsInteractor,
    settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState: MutableStateFlow<NotificationsUiState> = MutableStateFlow(
        NotificationsUiState.DEFAULT
    )
    val uiState: StateFlow<NotificationsUiState> = _uiState
        .combine(airingRepository.timeInMinutesFlow) { uiState, timeInMinutes ->
            uiState.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(notificationsRepository.notificationsFlow) { uiState, notifications ->
            uiState.copy(
                notifications = notifications
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, NotificationsUiState.DEFAULT)

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    fun resetNotificationsCounter() {
        viewModelScope.launch {
            notificationsRepository.resetUnreadNotificationsCounter()
        }
    }

    fun cancelStatusBarNotifications() {
        notificationsInteractor.clearStatusBarNotifications()
    }
}