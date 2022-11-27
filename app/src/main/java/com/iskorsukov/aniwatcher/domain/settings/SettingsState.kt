package com.iskorsukov.aniwatcher.domain.settings

data class SettingsState(
    val scheduleType: ScheduleType,
    val preferredNamingScheme: NamingScheme,
    val notificationsEnabled: Boolean
)
