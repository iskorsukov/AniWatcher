package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.MainActivityInputEvent
import javax.inject.Inject

sealed interface SeasonYearInputEvent: MainActivityInputEvent
data class SeasonYearSelectedEvent(val seasonYear: DateTimeHelper.SeasonYear): SeasonYearInputEvent

class SeasonYearEventHandler @Inject constructor() {

    fun handleEvent(
        inputEvent: SeasonYearInputEvent,
        originalUiState: MainActivityUiState,
        settingsRepository: SettingsRepository
    ): MainActivityUiState {
        return when (inputEvent) {
            is SeasonYearSelectedEvent -> {
                settingsRepository.setSelectedSeasonYear(inputEvent.seasonYear)
                originalUiState
            }
        }
    }
}