package com.iskorsukov.aniwatcher.domain.settings

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settingsStateFlow: StateFlow<SettingsState>

    fun onPreferenceChanged()

    fun setDarkModeOption(darkModeOption: DarkModeOption)

    fun setPreferredNamingScheme(preferredNamingScheme: NamingScheme)

    fun setScheduleType(scheduleType: ScheduleType)

    fun setOnboardingComplete(onboardingComplete: Boolean)
}