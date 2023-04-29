package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.MainDispatcherRule
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.exception.ApolloException
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.*
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val notificationsRepository: NotificationsRepository = mockk<NotificationsRepository>(relaxed = true).also {
        every { it.unreadNotificationsCounterStateFlow } returns MutableStateFlow(0)
    }

    private lateinit var viewModel: MainActivityViewModel

    @Test
    fun loadAiringData_season() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(
            DateTimeHelper.Season.FALL,
            2022
        )

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022)
                )

        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.dataFlow.collect {
                it.errorItem
            }
        }

        viewModel.loadAiringData()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
        collectorJob.cancel()
    }

    @Test
    fun loadAiringData_range() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentWeekStartToEndSeconds(any()) } returns (0 to 1)

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear.THIS_WEEK
                )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.dataFlow.collect {
                it.errorItem
            }
        }

        viewModel.loadAiringData()

        coVerify { airingRepository.loadRangeAiringData(0, 1) }

        unmockkAll()
        collectorJob.cancel()
    }

    @Test
    fun loadAiringData_exception() = runTest {
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockk(relaxed = true)

        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(
            DateTimeHelper.Season.FALL,
            2022
        )

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022)
                )

        coEvery { airingRepository.loadSeasonAiringData(any(), any()) } throws ApolloException(IOException())

        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.dataFlow.collect {
                it.errorItem
            }
        }

        viewModel.loadAiringData()

        val state = viewModel.dataFlow.value
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isInstanceOf(ErrorItem.LoadingData::class.java)

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
        collectorJob.cancel()
    }
}