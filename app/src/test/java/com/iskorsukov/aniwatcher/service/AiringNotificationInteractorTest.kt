package com.iskorsukov.aniwatcher.service

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.service.util.NotificationBuilderHelper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.airingAt
import com.iskorsukov.aniwatcher.test.id
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test

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

    private val airingNotificationInteractor = AiringNotificationInteractorImpl(
        context,
        airingRepository,
        notificationManagerCompat
    )

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

        unmockkObject(NotificationBuilderHelper)
    }
}