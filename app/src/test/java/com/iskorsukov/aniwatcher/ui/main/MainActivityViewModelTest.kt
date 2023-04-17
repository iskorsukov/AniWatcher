package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.*
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.AppendSearchTextInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchFieldVisibilityChangedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextChangedInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextEventHandler
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import java.io.IOException
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val notificationsRepository: NotificationsRepository = mockk(relaxed = true)
    private val searchTextEventHandler: SearchTextEventHandler<MainActivityUiState> = spyk(
        SearchTextEventHandler()
    )
    private val seasonYearEventHandler: SeasonYearEventHandler = spyk(SeasonYearEventHandler())
    private val notificationsPermissionEventHandler: NotificationsPermissionEventHandler = spyk(
        NotificationsPermissionEventHandler()
    )

    private lateinit var viewModel: MainActivityViewModel

    private fun initViewModel(testScheduler: TestCoroutineScheduler) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
    }

    @Test
    fun loadAiringData_season() = runTest {
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns GregorianCalendar().apply {
            timeInMillis = 1669870920000L
        }
        initViewModel(testScheduler)

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(
            DateTimeHelper.Season.FALL,
            2022
        )

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    scheduleType = ScheduleType.SEASON,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    onboardingComplete = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022)
                )

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.value.isRefreshing).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isRefreshing).isFalse()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
    }

    @Test
    fun loadAiringData_range() = runTest {
        initViewModel(testScheduler)

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentWeekStartToEndSeconds(any()) } returns (0 to 1)

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    scheduleType = ScheduleType.ALL,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    onboardingComplete = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022)
                )

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.value.isRefreshing).isTrue()

        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isRefreshing).isFalse()

        coVerify { airingRepository.loadRangeAiringData(0, 1) }

        unmockkAll()
    }

    @Test
    fun loadAiringData_exception() = runTest {
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockk(relaxed = true)
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns GregorianCalendar().apply {
            timeInMillis = 1669870920000L
        }
        initViewModel(testScheduler)

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentSeasonYear(any()) } returns DateTimeHelper.SeasonYear(
            DateTimeHelper.Season.FALL,
            2022
        )

        coEvery { settingsRepository.settingsStateFlow.value } returns
                SettingsState(
                    darkModeOption = DarkModeOption.SYSTEM,
                    scheduleType = ScheduleType.SEASON,
                    preferredNamingScheme = NamingScheme.ROMAJI,
                    notificationsEnabled = true,
                    onboardingComplete = true,
                    selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022)
                )

        coEvery { airingRepository.loadSeasonAiringData(any(), any()) } throws IOException()

        viewModel.loadAiringData()
        assertThat(viewModel.uiState.value.isRefreshing).isTrue()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isInstanceOf(ErrorItem.Unknown::class.java)

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        initViewModel(testScheduler)

        val searchText = "Search"

        assertThat(viewModel.uiState.first().searchText).isEmpty()

        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))
        assertThat(viewModel.uiState.value.searchText).isEqualTo(searchText)
        verify {
            searchTextEventHandler.handleEvent(
                SearchTextChangedInputEvent(
                    searchText
                ),
                MainActivityUiState.DEFAULT
            )
        }
    }

    @Test
    fun searchTextEvent_appendSearchText() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(AppendSearchTextInputEvent("Text"))

        assertThat(viewModel.uiState.value.searchText).isEqualTo("Text")
        verify {
            searchTextEventHandler.handleEvent(
                AppendSearchTextInputEvent(
                    "Text"
                ),
                MainActivityUiState.DEFAULT
            )
        }
    }

    @Test
    fun searchTextEvent_searchFieldOpenChange() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(SearchFieldVisibilityChangedInputEvent(true))

        assertThat(viewModel.uiState.value.searchFieldOpen).isTrue()
        verify {
            searchTextEventHandler.handleEvent(
                SearchFieldVisibilityChangedInputEvent(true),
                MainActivityUiState.DEFAULT
            )
        }
    }

    @Test
    fun seasonYearEvent_seasonYearSelected() = runTest {
        initViewModel(testScheduler)

        val selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023)
        viewModel.handleInputEvent(SeasonYearSelectedEvent(selectedSeasonYear))

        verify {
            seasonYearEventHandler.handleEvent(
                SeasonYearSelectedEvent(selectedSeasonYear),
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
        }
    }

    @Test
    fun notificationsPermissionEvent_missing() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(NotificationsPermissionMissing)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isTrue()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionMissing,
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    @Test
    fun notificationsPermissionEvent_grantClicked() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(NotificationsPermissionGrantClicked)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isTrue()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionGrantClicked,
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
        }
    }

    @Test
    fun notificationsPermissionEvent_denyClicked() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(NotificationsPermissionDisableClicked)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionDisableClicked,
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    @Test
    fun notificationsPermissionEvent_granted() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(NotificationsPermissionGranted)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionGranted,
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(true)
        }
    }

    @Test
    fun notificationsPermissionEvent_denied() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(NotificationsPermissionDenied)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionDenied,
                MainActivityUiState.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }
    }
}