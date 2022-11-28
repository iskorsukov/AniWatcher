package com.iskorsukov.aniwatcher.ui.notification

import com.iskorsukov.aniwatcher.domain.notification.NotificationsInteractor
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
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
class NotificationViewModelTest {

    private val notificationsRepository: NotificationsRepository = mockk(relaxed = true)
    private val notificationsInteractor: NotificationsInteractor = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)

    private val viewModel = NotificationsViewModel(
        notificationsRepository, notificationsInteractor, settingsRepository
    )

    @Test
    fun resetNotificationsCounter() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        viewModel.resetNotificationsCounter()
        advanceUntilIdle()

        coVerify {
            notificationsRepository.resetUnreadNotificationsCounter()
        }
    }

    @Test
    fun cancelStatusBarNotifications() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        viewModel.cancelStatusBarNotifications()
        advanceUntilIdle()

        coVerify {
            notificationsInteractor.clearStatusBarNotifications()
        }
    }
}