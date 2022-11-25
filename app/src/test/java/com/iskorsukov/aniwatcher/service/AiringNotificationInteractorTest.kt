package com.iskorsukov.aniwatcher.service

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.service.util.LocalClock
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
    private val clock: LocalClock = mockk<LocalClock>().apply {
        every { currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(0)
    }

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)

    private lateinit var settingsFlow: MutableStateFlow<SettingsState>
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var unreadNotificationsFlow: MutableStateFlow<Int>
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
                                .airingAt(TimeUnit.MINUTES.toSeconds(0L).toInt()),
                            ModelTestDataCreator.baseAiringScheduleItem()
                                .id(2)
                                .airingAt(TimeUnit.MINUTES.toSeconds(5L).toInt())
                        )
            )
        )

        settingsFlow = MutableStateFlow(SettingsState(NamingScheme.ENGLISH, true))
        settingsRepository = mockk<SettingsRepository>(relaxed = true).apply {
            coEvery { settingsStateFlow } returns settingsFlow
        }

        unreadNotificationsFlow = MutableStateFlow(0)
        notificationsRepository = mockk<NotificationsRepository>(relaxed = true).apply {
            coEvery { unreadNotificationsCounterStateFlow } returns unreadNotificationsFlow
        }

        airingNotificationInteractor = AiringNotificationInteractorImpl(
            context,
            clock,
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

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(1)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(6)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 1) {
            notificationManagerCompat.notify(1, any())
            notificationsRepository.saveNotification(
                match {
                    it.airingScheduleItem.id == 1
                }
            )
        }
        coVerify(exactly = 1) {
            notificationManagerCompat.notify(2, any())
            notificationsRepository.saveNotification(
                match {
                    it.airingScheduleItem.id == 2
                }
            )
        }
        coVerify(exactly = 2) {
            notificationsRepository.increaseUnreadNotificationsCounter()
        }

        cleanupMocks()
    }

    @Test
    fun notificationsDisabled() = runTest {
        initMocks(testScheduler)

        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)

        airingNotificationInteractor.startNotificationChecking()

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(1)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 0) {
            notificationManagerCompat.notify(1, any())
        }

        cleanupMocks()
    }

    @Test
    fun stopsWhenNotificationsDisabled() = runTest {
        initMocks(testScheduler)

        airingNotificationInteractor.startNotificationChecking()

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(1)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(1))
        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(6)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))
        airingNotificationInteractor.stopNotificationChecking()

        coVerify(exactly = 1) {
            notificationManagerCompat.notify(1, any())
        }

        cleanupMocks()
    }
}