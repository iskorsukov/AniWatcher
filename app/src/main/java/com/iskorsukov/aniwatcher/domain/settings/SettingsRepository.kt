package com.iskorsukov.aniwatcher.domain.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settingsStateFlow: StateFlow<SettingsState>

    fun onPreferenceChanged()
}