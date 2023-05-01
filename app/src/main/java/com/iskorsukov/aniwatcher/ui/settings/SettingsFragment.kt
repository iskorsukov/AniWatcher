package com.iskorsukov.aniwatcher.ui.settings

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            settingsRepository.onPreferenceChanged()
            if (key == getString(R.string.settings_dark_mode_key)) {
                activity?.recreate()
            } else if (key == getString(R.string.settings_notifications_enabled_key)) {
                val permissionBlocked = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED &&
                        !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                if (permissionBlocked) {
                    Toast.makeText(
                        requireContext(),
                        R.string.settings_notifications_enabled_permission_block,
                        Toast.LENGTH_LONG
                    ).show()
                    settingsRepository.setNotificationsEnabled(false)
                }
            }
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
        findPreference<ListPreference>(
            getString(R.string.settings_dark_mode_key)
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