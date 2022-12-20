package com.iskorsukov.aniwatcher.domain.settings

import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper

data class SettingsState(
    val darkModeOption: DarkModeOption,
    val scheduleType: ScheduleType,
    val preferredNamingScheme: NamingScheme,
    val notificationsEnabled: Boolean,
    val onboardingComplete: Boolean,
    val selectedSeasonYear: DateTimeHelper.SeasonYear
)
