package com.iskorsukov.aniwatcher.domain.notification.work

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.notification.work.util.LocalClockSystem
import com.iskorsukov.aniwatcher.domain.notification.work.util.NotificationBuilderHelper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.airingAt
import com.iskorsukov.aniwatcher.test.id
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class NotificationsWorkerTest {

    private lateinit var context: Context

    private val clock: LocalClockSystem = mockk<LocalClockSystem>().apply {
        every { currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(10L)
    }

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)

    private lateinit var notificationsRepository: NotificationsRepository

    private lateinit var notificationsWorker: NotificationsWorker

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        mockkObject(NotificationBuilderHelper)
        every { NotificationBuilderHelper.buildNotification(any(), any()) } returns mockk()

        coEvery { notificationsRepository.getPendingSchedulesToNotifyFlow() } returns flowOf(
            listOf(
                ModelTestDataCreator.baseAiringScheduleItem()
                    .airingAt(TimeUnit.MINUTES.toSeconds(0L).toInt()),
                ModelTestDataCreator.baseAiringScheduleItem()
                    .id(2)
                    .airingAt(TimeUnit.MINUTES.toSeconds(12L).toInt())
            )
        )

        notificationsRepository = mockk(relaxed = true)

        notificationsWorker = TestListenableWorkerBuilder<NotificationsWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return NotificationsWorker(
                        context,
                        workerParameters,
                        clock,
                        notificationsRepository,
                        notificationManagerCompat
                    )
                }
            })
            .build()
    }

    @After
    fun tearDown() {
        unmockkObject(NotificationBuilderHelper)
    }

    @Test
    fun firesNotificationIfNeeded() = runBlocking {
        notificationsWorker.doWork()

        every { clock.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(25L)
        notificationsWorker.doWork()

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
    }
}