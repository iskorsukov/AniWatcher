package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity
import com.iskorsukov.aniwatcher.data.room.*
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersistentMediaDatabaseExecutorTest {

    private val persistentMediaDao: PersistentMediaDao = mockk(relaxed = true)
    private val notificationsDao: NotificationsDao = mockk(relaxed = true)
    private val persistentMediaDatabase: PersistentMediaDatabase = mockk(relaxed = true)

    private val clock: LocalClockSystem = mockk(relaxed = true)

    private lateinit var persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor

    private val testDataPair = Pair(
        EntityTestDataCreator.mediaItemEntity(mediaId = 1),
        listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
    )

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")

        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        every { persistentMediaDatabase.persistentMediaDao() } returns persistentMediaDao
        every { persistentMediaDatabase.notificationsDao() } returns notificationsDao
        every { clock.currentTimeSeconds() } returns Int.MAX_VALUE

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { persistentMediaDatabase.withTransaction(capture(transactionLambda)) } coAnswers  {
            transactionLambda.captured.invoke()
        }

        persistentMediaDatabaseExecutor = PersistentMediaDatabaseExecutor(
            persistentMediaDatabase,
            clock
        )
    }

    private fun cleanupMocks() {
        unmockkAll()
    }

    @Test
    fun saveMediaWithSchedules() = runTest {
        initMocks(testScheduler)

        persistentMediaDatabaseExecutor.saveMediaWithSchedules(
            testDataPair
        )
        advanceUntilIdle()

        coVerifyOrder {
            persistentMediaDao.insertMedia(testDataPair.first)
            persistentMediaDao.insertSchedules(testDataPair.second)
        }

        cleanupMocks()
    }

    @Test
    fun deleteMedia() = runTest {
        initMocks(testScheduler)

        persistentMediaDatabaseExecutor.deleteMedia(1)

        coVerifyOrder {
            persistentMediaDao.deleteNotifications(1)
            persistentMediaDao.deleteSchedules(1)
            persistentMediaDao.deleteMedia(1)
        }

        cleanupMocks()
    }

    @Test
    fun saveNotification() = runTest {
        initMocks(testScheduler)

        val notificationItem = ModelTestDataCreator.notificationItem(
            null,
            airingScheduleItem = ModelTestDataCreator.airingScheduleItem(id = 1),
            mediaItem = ModelTestDataCreator.mediaItem(id = 1)
        )

        persistentMediaDatabaseExecutor.saveNotification(notificationItem)

        coVerify {
            notificationsDao.insertNotification(
                NotificationItemEntity(
                    notificationItem.id,
                    notificationItem.firedAtMillis,
                    notificationItem.airingScheduleItem.id
                )
            )
        }

        cleanupMocks()
    }
}