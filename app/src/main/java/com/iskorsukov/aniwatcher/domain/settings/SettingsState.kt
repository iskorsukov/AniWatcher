package com.iskorsukov.aniwatcher.domain.settings

data class SettingsState(
    val darkModeOption: DarkModeOption,
    val scheduleType: ScheduleType,
    val preferredNamingScheme: NamingScheme,
    val notificationsEnabled: Boolean,
    val onboardingComplete: Boolean
)
