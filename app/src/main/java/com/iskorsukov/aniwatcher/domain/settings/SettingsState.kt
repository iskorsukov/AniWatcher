package com.iskorsukov.aniwatcher.domain.settings

import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper

data class SettingsState(
    val darkModeOption: DarkModeOption,
    val preferredNamingScheme: NamingScheme,
    val notificationsEnabled: Boolean,
    val selectedSeasonYear: DateTimeHelper.SeasonYear
)
