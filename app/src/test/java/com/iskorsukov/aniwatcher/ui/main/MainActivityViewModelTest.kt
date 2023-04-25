package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.MainDispatcherRule
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
    private val searchTextEventHandler: SearchTextEventHandler<MainScreenData> = spyk(
        SearchTextEventHandler()
    )
    private val seasonYearEventHandler: SeasonYearEventHandler = spyk(SeasonYearEventHandler())
    private val notificationsPermissionEventHandler: NotificationsPermissionEventHandler = spyk(
        NotificationsPermissionEventHandler()
    )

    private lateinit var viewModel: MainActivityViewModel

    @Test
    fun loadAiringData_season() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
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
            viewModel.uiState.collect {
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
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
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
            viewModel.uiState.collect {
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
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
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

        coEvery { airingRepository.loadSeasonAiringData(any(), any()) } throws IOException()

        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.loadAiringData()

        val state = viewModel.uiState.value
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.errorItem).isInstanceOf(ErrorItem.Unknown::class.java)

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
        collectorJob.cancel()
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )

        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        val searchText = "Search"
        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))

        assertThat(viewModel.uiState.value.searchText).isEqualTo(searchText)
        verify {
            searchTextEventHandler.handleEvent(
                SearchTextChangedInputEvent(
                    searchText
                ),
                MainScreenData.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun searchTextEvent_appendSearchText() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(AppendSearchTextInputEvent("Text"))

        assertThat(viewModel.uiState.value.searchText).isEqualTo("Text")
        verify {
            searchTextEventHandler.handleEvent(
                AppendSearchTextInputEvent(
                    "Text"
                ),
                MainScreenData.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun searchTextEvent_searchFieldOpenChange() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(SearchFieldVisibilityChangedInputEvent(true))

        assertThat(viewModel.uiState.value.searchFieldOpen).isTrue()
        verify {
            searchTextEventHandler.handleEvent(
                SearchFieldVisibilityChangedInputEvent(true),
                MainScreenData.DEFAULT
            )
        }
        collectorJob.cancel()
    }

    @Test
    fun seasonYearEvent_seasonYearSelected() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        val selectedSeasonYear = DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023)
        viewModel.handleInputEvent(SeasonYearSelectedEvent(selectedSeasonYear))

        verify {
            seasonYearEventHandler.handleEvent(
                SeasonYearSelectedEvent(selectedSeasonYear),
                MainScreenData.DEFAULT,
                settingsRepository
            )
        }
        collectorJob.cancel()
    }

    @Test
    fun notificationsPermissionEvent_missing() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(NotificationsPermissionMissing)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isTrue()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionMissing,
                MainScreenData.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }
        collectorJob.cancel()
    }

    @Test
    fun notificationsPermissionEvent_grantClicked() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(NotificationsPermissionGrantClicked)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isTrue()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionGrantClicked,
                MainScreenData.DEFAULT,
                settingsRepository
            )
        }
        collectorJob.cancel()
    }

    @Test
    fun notificationsPermissionEvent_denyClicked() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(NotificationsPermissionDisableClicked)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionDisableClicked,
                MainScreenData.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }

        collectorJob.cancel()
    }

    @Test
    fun notificationsPermissionEvent_granted() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(NotificationsPermissionGranted)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionGranted,
                MainScreenData.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(true)
        }

        collectorJob.cancel()
    }

    @Test
    fun notificationsPermissionEvent_denied() = runTest {
        viewModel = MainActivityViewModel(
            airingRepository,
            settingsRepository,
            notificationsRepository,
            searchTextEventHandler,
            seasonYearEventHandler,
            notificationsPermissionEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {
                it.errorItem
            }
        }

        viewModel.handleInputEvent(NotificationsPermissionDenied)

        assertThat(viewModel.uiState.value.showNotificationsPermissionRationale).isFalse()
        assertThat(viewModel.uiState.value.launchNotificationPermissionRequest).isFalse()
        verify {
            notificationsPermissionEventHandler.handleEvent(
                NotificationsPermissionDenied,
                MainScreenData.DEFAULT,
                settingsRepository
            )
            settingsRepository.setNotificationsEnabled(false)
        }

        collectorJob.cancel()
    }
}