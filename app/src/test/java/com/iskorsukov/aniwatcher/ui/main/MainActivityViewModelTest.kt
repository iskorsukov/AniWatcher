package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepositoryImpl
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
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

    private val airingRepositoryImpl: AiringRepositoryImpl = mockk(relaxed = true)

    private val viewModel = MainActivityViewModel(airingRepositoryImpl)

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

        coVerify { airingRepositoryImpl.loadSeasonAiringData(2022, "FALL") }

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun loadAiringData_exception() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentYear(any()) } returns 2022
        every { DateTimeHelper.currentSeason(any()) } returns "FALL"

        coEvery { airingRepositoryImpl.loadSeasonAiringData(any(), any()) } throws IOException()

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()
        advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isNotNull()

        coVerify { airingRepositoryImpl.loadSeasonAiringData(2022, "FALL") }

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun onSearchTextInput() = runTest {
        val searchText = "Search"

        assertThat(viewModel.searchTextState.first()).isEmpty()

        viewModel.onSearchTextInput(searchText)
        assertThat(viewModel.searchTextState.first()).isEqualTo(searchText)
    }

    @Test
    fun onSortingOptionsIconClicked() = runTest {
        viewModel.onSortingOptionsIconClicked()

        val state = viewModel.uiState.first()
        assertThat(state.showSortingOptionsDialog).isTrue()
    }

    @Test
    fun onSortingOptionSelected() = runTest {
        viewModel.onSortingOptionsIconClicked()

        val state = viewModel.uiState.first()
        assertThat(state.showSortingOptionsDialog).isTrue()

        viewModel.onSortingOptionSelected(SortingOption.SCORE)
        assertThat(viewModel.sortingOptionState.first()).isEqualTo(SortingOption.SCORE)

    }

    @Test
    fun onSortingOptionsDialogDismissed() = runTest {
        viewModel.onSortingOptionsIconClicked()

        var state = viewModel.uiState.first()
        assertThat(state.showSortingOptionsDialog).isTrue()

        viewModel.onSortingOptionsDialogDismissed()

        state = viewModel.uiState.first()
        assertThat(state.showSortingOptionsDialog).isFalse()
    }
}