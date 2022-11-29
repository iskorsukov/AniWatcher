package com.iskorsukov.aniwatcher.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.settings.DarkModeOption
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsCompatActivity: AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(getCurrentTheme(), true)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @StyleRes
    fun getCurrentTheme(): Int {
        var darkModeOption = settingsRepository.settingsStateFlow.value.darkModeOption
        if (darkModeOption == DarkModeOption.SYSTEM) {
            darkModeOption = when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> DarkModeOption.DARK
                else -> DarkModeOption.LIGHT
            }
        }
        return if (darkModeOption == DarkModeOption.DARK) {
            R.style.Theme_AniWatcher_AppCompat_Dark
        } else {
            R.style.Theme_AniWatcher_AppCompat
        }
    }
}