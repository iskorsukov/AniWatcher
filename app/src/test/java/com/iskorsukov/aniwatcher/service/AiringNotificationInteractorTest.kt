package com.iskorsukov.aniwatcher.service

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.service.util.NotificationBuilderHelper
import com.iskorsukov.aniwatcher.test.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class AiringNotificationInteractorTest {

    private val context: Context = mockk<Context>(relaxed = true).apply {
        every { getString(any()) } returns "Some string"
        every { getString(any(), any()) } returns "Some string"
    }
    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)

    private lateinit var settingsFlow: MutableStateFlow<SettingsState>
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var unreadNotificationsFlow: MutableStateFlow<Int>
    private lateinit var notificationsFlow: MutableStateFlow<List<NotificationItem>>
    private lateinit var notificationsRepository: NotificationsRepository

    private lateinit var airingNotificationInteractor: AiringNotificationInteractorImpl

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.default() } returns dispatcher

        mockkObject(NotificationBuilderHelper)
        every { NotificationBuilderHelper.buildNotification(any(), any()) } returns mockk()

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        listOf(
                            ModelTestDataCreator.baseAiringScheduleItem()
                                .airingAt((System.currentTimeMillis() / 1000).toInt() - 50 * 60 * 1000),
                            ModelTestDataCreator.baseAiringScheduleItem()
                                .id(2)
                                .airingAt((System.currentTimeMillis() / 1000).toInt() + 50 * 60 * 1000)
                        )
            )
        )

        settingsFlow = MutableStateFlow(SettingsState(NamingScheme.ENGLISH, true))
        settingsRepository = mockk<SettingsRepository>(relaxed = true).apply {
            coEvery { settingsStateFlow } returns settingsFlow
        }

        unreadNotificationsFlow = MutableStateFlow(0)
        notificationsFlow = MutableStateFlow(emptyList())
        notificationsRepository = mockk<NotificationsRepository>(relaxed = true).apply {
            coEvery { unreadNotificationsCounterStateFlow } returns unreadNotificationsFlow
            coEvery { notificationsFlow } returns this@AiringNotificationInteractorTest.notificationsFlow
        }

        airingNotificationInteractor = AiringNotificationInteractorImpl(
            context,
            airingRepository,
            notificationsRepository,
            notificationManagerCompat,
            settingsRepository,
        )
    }

    private fun cleanupMocks() {
        Dispatchers.resetMain()
        unmockkObject(NotificationBuilderHelper)
        unmockkObject(DispatcherProvider)
    }

    @Test
    fun firesNotificationIfNeeded() = runTest {
        initMocks(testScheduler)

        airingNotificationInteractor.startNotificationChecking()

        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 3) {
            airingRepository.clearAiredSchedules()
        }
        coVerify(exactly = 2) {
            notificationManagerCompat.notify(1, any())
        }
        coVerify(exactly = 0) {
            notificationManagerCompat.notify(2, any())
        }

        cleanupMocks()
    }

    @Test
    fun notificationsDisabled() = runTest {
        initMocks(testScheduler)

        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)

        airingNotificationInteractor.startNotificationChecking()

        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 0) {
            airingRepository.clearAiredSchedules()
            notificationManagerCompat.notify(1, any())
        }

        cleanupMocks()
    }

    @Test
    fun stopsWhenNotificationsDisabled() = runTest {
        initMocks(testScheduler)

        airingNotificationInteractor.startNotificationChecking()

        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)

        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 2) {
            airingRepository.clearAiredSchedules()
        }
        coVerify(exactly = 1) {
            notificationManagerCompat.notify(1, any())
        }

        cleanupMocks()
    }

    @Test
    fun filtersSchedulesWhereNotificationWasAlreadyFired() = runTest {
        initMocks(testScheduler)

        airingNotificationInteractor.startNotificationChecking()

        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        notificationsFlow.value = listOf(ModelTestDataCreator.baseNotificationItem())

        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 3) {
            airingRepository.clearAiredSchedules()
        }
        coVerify(exactly = 1) {
            notificationManagerCompat.notify(1, any())
        }

        cleanupMocks()
    }
}