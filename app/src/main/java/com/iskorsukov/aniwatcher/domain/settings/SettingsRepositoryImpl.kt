package com.iskorsukov.aniwatcher.domain.settings

import android.content.Context
import android.content.SharedPreferences
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
): SettingsRepository {

    private var selectedSeasonYear = DateTimeHelper.SeasonYear.THIS_WEEK

    private val _settingsStateFlow: MutableStateFlow<SettingsState> = MutableStateFlow(
        SettingsState(
            getDarkModeOption(),
            getPreferredNamingScheme(),
            getNotificationsEnabled(),
            selectedSeasonYear
        )
    )
    override val settingsStateFlow: StateFlow<SettingsState> = _settingsStateFlow

    override fun onPreferenceChanged() {
        _settingsStateFlow.value = SettingsState(
            getDarkModeOption(),
            getPreferredNamingScheme(),
            getNotificationsEnabled(),
            selectedSeasonYear
        )
    }

    override fun setDarkModeOption(darkModeOption: DarkModeOption) {
        sharedPreferences.edit()
            .putString(
                context.getString(R.string.settings_dark_mode_key),
                darkModeOption.name
            )
            .apply()
        _settingsStateFlow.value = _settingsStateFlow.value.copy(darkModeOption = darkModeOption)
    }

    override fun setPreferredNamingScheme(preferredNamingScheme: NamingScheme) {
        sharedPreferences.edit()
            .putString(
                context.getString(R.string.settings_naming_scheme_key),
                preferredNamingScheme.name
            )
            .apply()
        _settingsStateFlow.value = _settingsStateFlow.value.copy(preferredNamingScheme = preferredNamingScheme)
    }
    override fun setSelectedSeasonYear(seasonYear: DateTimeHelper.SeasonYear) {
        selectedSeasonYear = seasonYear
        _settingsStateFlow.value = _settingsStateFlow.value.copy(selectedSeasonYear = selectedSeasonYear)
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(
                context.getString(R.string.settings_notifications_enabled_key),
                enabled
            )
            .apply()
        _settingsStateFlow.value = _settingsStateFlow.value.copy(notificationsEnabled = enabled)
    }

    private fun getDarkModeOption(): DarkModeOption {
        return DarkModeOption.valueOf(
            sharedPreferences.getString(
                context.getString(R.string.settings_dark_mode_key),
                context.getString(R.string.dark_mode_default_value)
            )!!
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