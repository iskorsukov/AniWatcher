package com.iskorsukov.aniwatcher.service

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.service.util.NotificationBuilderHelper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.airingAt
import com.iskorsukov.aniwatcher.test.id
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class AiringNotificationInteractorTest {

    private val context: Context = mockk<Context>(relaxed = true).apply {
        every { getString(any()) } returns "Some string"
        every { getString(any(), any()) } returns "Some string"
    }
    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).apply {
        coEvery { mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        listOf(
                            ModelTestDataCreator.baseAiringScheduleItem(),
                            ModelTestDataCreator.baseAiringScheduleItem()
                                .id(2)
                                .airingAt((System.currentTimeMillis() / 1000).toInt() + 1000)
                        )
            )
        )
    }
    private val notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)

    private lateinit var settingsFlow: MutableStateFlow<SettingsState>
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var unreadNotificationsFlow: MutableStateFlow<Int>
    private lateinit var notificationsRepository: NotificationsRepository

    private lateinit var airingNotificationInteractor: AiringNotificationInteractorImpl

    @Before
    fun initMocks() {
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
            airingRepository,
            notificationsRepository,
            notificationManagerCompat,
            settingsRepository,
        )
    }

    @Test
    fun firesNotificationIfNeeded() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        mockkObject(NotificationBuilderHelper)
        every { NotificationBuilderHelper.buildNotification(any(), any()) } returns mockk()

        airingNotificationInteractor.startNotificationChecking()
        advanceUntilIdle()

        coVerify {
            airingRepository.clearAiredSchedules()
            notificationManagerCompat.notify(1, any())
        }
        coVerify(exactly = 0) {
            notificationManagerCompat.notify(2, any())
        }

        unmockkObject(NotificationBuilderHelper)
    }

    @Test
    fun notificationsDisabled() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)

        airingNotificationInteractor.startNotificationChecking()
        advanceUntilIdle()

        coVerify(exactly = 0) {
            airingRepository.clearAiredSchedules()
            notificationManagerCompat.notify(1, any())
        }
    }

    @Test
    fun stopsWhenNotificationsDisabled() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.default() } returns dispatcher

        // construct with mock dispatcher
        airingNotificationInteractor = AiringNotificationInteractorImpl(
            context,
            airingRepository,
            notificationsRepository,
            notificationManagerCompat,
            settingsRepository
        )

        mockkObject(NotificationBuilderHelper)
        every { NotificationBuilderHelper.buildNotification(any(), any()) } returns mockk()

        airingNotificationInteractor.startNotificationChecking()
        advanceTimeBy(TimeUnit.MINUTES.toMillis(2))

        settingsFlow.value = SettingsState(NamingScheme.ENGLISH, false)
        advanceTimeBy(TimeUnit.MINUTES.toMillis(5))

        coVerify(exactly = 1) {
            notificationManagerCompat.notify(1, any())
        }

        unmockkObject(NotificationBuilderHelper)
        unmockkObject(DispatcherProvider)
    }
}