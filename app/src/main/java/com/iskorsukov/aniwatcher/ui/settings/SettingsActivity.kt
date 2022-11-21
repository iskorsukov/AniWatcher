package com.iskorsukov.aniwatcher.ui.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.databinding.SettingsActivityBinding
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.base.topbar.BackArrowTopAppBar
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        BackArrowTopAppBar { finish() }
                    },
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    AndroidViewBinding(
                        factory = SettingsActivityBinding::inflate,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {

        @Inject
        lateinit var settingsRepository: SettingsRepository

        @Inject
        lateinit var sharedPreferences: SharedPreferences

        private val onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, _ ->
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
        }

        override fun onDestroy() {
            super.onDestroy()
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                onSharedPreferenceChangeListener
            )
        }
    }
}