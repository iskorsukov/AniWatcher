package com.iskorsukov.aniwatcher.ui.main

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
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
class NotificationsPermissionEventHandlerTest {
    private val notificationsPermissionEventHandler: NotificationsPermissionEventHandler =
        NotificationsPermissionEventHandler()

    private val settingsRepository: SettingsRepository = mockk(relaxed = true)

    @Test
    fun handleEvent_notificationsPermissionMissing() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = notificationsPermissionEventHandler.handleEvent(
            NotificationsPermissionMissing,
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState.showNotificationsPermissionRationale).isTrue()
        coVerify {
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    @Test
    fun handleEvent_notificationsPermissionGrantClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = notificationsPermissionEventHandler.handleEvent(
            NotificationsPermissionGrantClicked,
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState.showNotificationsPermissionRationale).isFalse()
        assertThat(uiState.launchNotificationPermissionRequest).isTrue()
    }

    @Test
    fun handleEvent_notificationsPermissionDisableClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = notificationsPermissionEventHandler.handleEvent(
            NotificationsPermissionDisableClicked,
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState.showNotificationsPermissionRationale).isFalse()
        assertThat(uiState.launchNotificationPermissionRequest).isFalse()
        coVerify {
            settingsRepository.setNotificationsEnabled(false)
        }
    }

    @Test
    fun handleEvent_notificationsPermissionGranted() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = notificationsPermissionEventHandler.handleEvent(
            NotificationsPermissionGranted,
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState.showNotificationsPermissionRationale).isFalse()
        assertThat(uiState.launchNotificationPermissionRequest).isFalse()
        coVerify {
            settingsRepository.setNotificationsEnabled(true)
        }
    }

    @Test
    fun handleEvent_notificationsPermissionDenied() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = notificationsPermissionEventHandler.handleEvent(
            NotificationsPermissionDenied,
            MainActivityUiState.DEFAULT,
            settingsRepository
        )
        advanceUntilIdle()

        assertThat(uiState.showNotificationsPermissionRationale).isFalse()
        assertThat(uiState.launchNotificationPermissionRequest).isFalse()
        coVerify {
            settingsRepository.setNotificationsEnabled(false)
        }
    }
}