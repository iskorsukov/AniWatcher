package com.iskorsukov.aniwatcher.domain.settings

import android.content.Context
import android.content.SharedPreferences
import com.iskorsukov.aniwatcher.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
): SettingsRepository {

    private val _settingsStateFlow: MutableStateFlow<SettingsState> = MutableStateFlow(
        SettingsState(
            getPreferredNamingScheme(),
            getNotificationsEnabled()
        )
    )
    override val settingsStateFlow: StateFlow<SettingsState> = _settingsStateFlow

    override fun onPreferenceChanged() {
        _settingsStateFlow.value = SettingsState(
            getPreferredNamingScheme(),
            getNotificationsEnabled()
        )
    }

    private fun getPreferredNamingScheme(): NamingScheme {
        return NamingScheme.valueOf(
            sharedPreferences.getString(
                context.getString(R.string.settings_naming_scheme_key),
                context.getString(R.string.naming_scheme_default_value)
            )!!
        )
    }

    private fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(
            context.getString(R.string.settings_notifications_enabled_key),
            true
        )
    }
}