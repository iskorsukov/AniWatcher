package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private val viewModel = MainActivityViewModel(airingRepository)

    @Test
    fun loadAiringData() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentYear(any()) } returns 2022
        every { DateTimeHelper.currentSeason(any()) } returns "FALL"

        viewModel.loadAiringData()
        assertThat(viewModel.refreshingState.first()).isTrue()
        advanceUntilIdle()
        assertThat(viewModel.refreshingState.first()).isFalse()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
    }
}