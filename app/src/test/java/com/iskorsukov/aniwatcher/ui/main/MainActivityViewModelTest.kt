package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.io.IOException

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
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()
        advanceUntilIdle()
        assertThat(viewModel.uiState.first().isRefreshing).isFalse()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
    }

    @Test
    fun loadAiringData_exception() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentYear(any()) } returns 2022
        every { DateTimeHelper.currentSeason(any()) } returns "FALL"

        coEvery { airingRepository.loadSeasonAiringData(any(), any()) } throws IOException()

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()
        advanceUntilIdle()
        val state = viewModel.uiState.first()
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isNotNull()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
    }
}