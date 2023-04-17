package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeasonYearEventHandlerTest {
    private val seasonYearEventHandler: SeasonYearEventHandler = SeasonYearEventHandler()

    private val settingsRepository: SettingsRepository = mockk(relaxed = true)

    @Test
    fun handleEvent_sortingOptionChanged() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023)
        val uiState = seasonYearEventHandler.handleEvent(
            SeasonYearSelectedEvent(selectedSeasonYear),
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState).isEqualTo(MainActivityUiState.DEFAULT)
        coVerify {
            settingsRepository.setSelectedSeasonYear(selectedSeasonYear)
        }
    }
}