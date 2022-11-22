package com.iskorsukov.aniwatcher.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iskorsukov.aniwatcher.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsCompatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)
    }
}