package com.iskorsukov.aniwatcher.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val onSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            settingsRepository.onPreferenceChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences.registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_menu_settings, rootKey)
        findPreference<ListPreference>(
            getString(R.string.settings_naming_scheme_key)
        )?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
        findPreference<ListPreference>(
            getString(R.string.settings_schedule_type_key)
        )?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
    }
}