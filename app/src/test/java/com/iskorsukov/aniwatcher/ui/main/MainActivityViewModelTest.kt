package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.*
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
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
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val notificationsRepository: NotificationsRepository = mockk(relaxed = true)

    private lateinit var viewModel: MainActivityViewModel

    @Test
    fun loadAiringData_season() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns GregorianCalendar().apply {
            timeInMillis = 1669870920000L
        }
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023)

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    DarkModeOption.SYSTEM,
                    ScheduleType.SEASON,
                    NamingScheme.ROMAJI,
                    true,
                    true
                )

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.uiState.first().isRefreshing).isFalse()

        coVerify { airingRepository.loadSeasonAiringData(2023, "WINTER") }

        unmockkAll()
    }

    @Test
    fun loadAiringData_range() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentWeekStartToEndSeconds(any()) } returns (0 to 1)

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    DarkModeOption.SYSTEM,
                    ScheduleType.ALL,
                    NamingScheme.ROMAJI,
                    true,
                    true
                )

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.uiState.first().isRefreshing).isFalse()

        coVerify { airingRepository.loadRangeAiringData(0, 1) }

        unmockkAll()
    }

    @Test
    fun loadAiringData_exception() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockk(relaxed = true)
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns GregorianCalendar().apply {
            timeInMillis = 1669870920000L
        }
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023)

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    DarkModeOption.SYSTEM,
                    ScheduleType.SEASON,
                    NamingScheme.ROMAJI,
                    true,
                    true
                )

        coEvery { airingRepository.loadSeasonAiringData(any(), any()) } throws IOException()

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.first().isRefreshing).isTrue()
        advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isInstanceOf(ErrorItem.Unknown::class.java)

        coVerify { airingRepository.loadSeasonAiringData(2023, "WINTER") }

        unmockkAll()
    }

    @Test
    fun onSearchTextInput() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        val searchText = "Search"

        assertThat(viewModel.uiState.first().searchText).isEmpty()

        viewModel.onSearchTextInput(searchText)
        assertThat(viewModel.uiState.first().searchText).isEqualTo(searchText)
    }

    @Test
    fun appendSearchText() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        viewModel.appendSearchText("Text")

        assertThat(viewModel.uiState.first().searchText).isEqualTo("Text")
    }

    @Test
    fun onSearchFieldOpenChange() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        viewModel.onSearchFieldOpenChange(true)

        assertThat(viewModel.uiState.first().searchFieldOpen).isTrue()
    }
}