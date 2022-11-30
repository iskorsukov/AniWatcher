package com.iskorsukov.aniwatcher.ui.base.viewmodel.onboarding

import androidx.lifecycle.ViewModel
import com.iskorsukov.aniwatcher.domain.settings.DarkModeOption
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.base.viewmodel.error.ErrorFlowViewModel

abstract class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel(), ErrorFlowViewModel {

    fun onDarkModeOptionSelected(darkModeOption: DarkModeOption) {
        settingsRepository.setDarkModeOption(darkModeOption)
    }

    fun onScheduleTypeSelected(scheduleType: ScheduleType) {
        settingsRepository.setScheduleType(scheduleType)
    }

    fun onPreferredNamingSchemeSelected(namingScheme: NamingScheme) {
        settingsRepository.setPreferredNamingScheme(namingScheme)
    }

    fun onOnboardingComplete() {
        settingsRepository.setOnboardingComplete(true)
    }
}