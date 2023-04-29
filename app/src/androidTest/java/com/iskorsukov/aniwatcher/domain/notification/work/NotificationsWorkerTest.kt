package com.iskorsukov.aniwatcher.domain.notification.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.iskorsukov.aniwatcher.domain.notification.NotificationsInteractor
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.notification.work.util.NotificationBuilderHelper
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationsWorkerTest {

    private lateinit var context: Context

    private val notificationsInteractor: NotificationsInteractor = mockk(relaxed = true)

    private lateinit var notificationsRepository: NotificationsRepository

    private lateinit var notificationsWorker: NotificationsWorker

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        mockkObject(NotificationBuilderHelper)
        every { NotificationBuilderHelper.buildNotification(any(), any(), any()) } returns mockk()

        notificationsRepository = mockk(relaxed = true)
        val firstPendingPair = Pair(
            ModelTestDataCreator.airingScheduleItem(id = 1),
            ModelTestDataCreator.mediaItem(id = 1)
        )
        val secondPendingPair = Pair(
            ModelTestDataCreator.airingScheduleItem(id = 2),
            ModelTestDataCreator.mediaItem(id = 2)
        )
        val pendingSchedulesList = listOf(
            firstPendingPair,
            secondPendingPair
        )
        coEvery { notificationsRepository.getPendingSchedulesToNotify() } returns pendingSchedulesList

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
                        LocalClockSystem(),
                        notificationsRepository,
                        notificationsInteractor
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
    fun firesPendingNotifications() = runBlocking {
        notificationsWorker.doWork()

        coVerify(exactly = 1) {
            notificationsRepository.saveNotification(
                match {
                    it.airingScheduleItem.id == 1
                }
            )
        }
        coVerify(exactly = 1) {
            notificationsRepository.saveNotification(
                match {
                    it.airingScheduleItem.id == 2
                }
            )
        }
        coVerify(exactly = 2) {
            notificationsRepository.increaseUnreadNotificationsCounter()
        }
        coVerify(exactly = 1) {
            notificationsInteractor.fireAiredNotifications(
                match {
                    it.size == 2
                }
            )
        }
    }
}