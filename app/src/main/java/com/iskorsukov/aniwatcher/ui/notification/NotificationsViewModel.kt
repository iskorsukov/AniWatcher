package com.iskorsukov.aniwatcher.ui.notification

import androidx.lifecycle.ViewModel
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    settingsRepository: SettingsRepository
): ViewModel() {
    val notificationsFlow = notificationsRepository.notificationsFlow
    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow
}