package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.room.*
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    private val followingMediaFlowData = mapOf(
        EntityTestDataCreator.baseMediaItemEntity() to EntityTestDataCreator.baseAiringScheduleEntityList()
    )
    private val notificationFlowData = mapOf(
        EntityTestDataCreator.baseMediaItemEntity() to
                listOf(
                    AiringScheduleAndNotificationEntity(
                        EntityTestDataCreator.baseAiringScheduleEntity(),
                        EntityTestDataCreator.baseNotificationEntity()
                    )
                )
    )
    private val pendingFlowData = mapOf(
        EntityTestDataCreator.baseMediaItemEntity() to listOf(EntityTestDataCreator.baseAiringScheduleEntity())
    )

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")

        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        every { persistentMediaDatabase.persistentMediaDao() } returns persistentMediaDao
        every { persistentMediaDao.getAll() } returns flowOf(followingMediaFlowData)
        every { persistentMediaDao.getById(any()) } returns flowOf(followingMediaFlowData)

        every { persistentMediaDatabase.notificationsDao() } returns notificationsDao
        every { notificationsDao.getAll() } returns flowOf(notificationFlowData)
        coEvery { notificationsDao.getPending(any()) } returns pendingFlowData

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
        unmockkObject(DispatcherProvider)
    }

    @Test
    fun followingMediaFlow() = runTest {
        initMocks(testScheduler)

        val followingMedia = persistentMediaDatabaseExecutor.followedMediaFlow.first()

        assertThat(followingMedia).isEqualTo(followingMediaFlowData)

        cleanupMocks()
    }

    @Test
    fun saveMediaWithSchedules() = runTest {
        initMocks(testScheduler)

        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        persistentMediaDatabaseExecutor.saveMediaWithSchedules(
            mediaItemEntity to airingScheduleEntityList
        )
        advanceUntilIdle()

        coVerify {
            persistentMediaDao.insertMedia(mediaItemEntity)
            persistentMediaDao.insertSchedules(airingScheduleEntityList)
        }

        cleanupMocks()
    }

    @Test
    fun deleteMedia() = runTest {
        initMocks(testScheduler)

        persistentMediaDatabaseExecutor.deleteMedia(1)

        coVerify {
            persistentMediaDao.deleteMedia(1)
        }

        cleanupMocks()
    }

    @Test
    fun notificationsFlow() = runTest {
        initMocks(testScheduler)

        val notifiedMediaMap = persistentMediaDatabaseExecutor.notificationsFlow.first()

        assertThat(notifiedMediaMap).isEqualTo(notificationFlowData)

        coVerify {
            notificationsDao.getAll()
        }

        cleanupMocks()
    }

    @Test
    fun saveNotification() = runTest {
        initMocks(testScheduler)

        val notificationItem = ModelTestDataCreator.baseNotificationItem()

        persistentMediaDatabaseExecutor.saveNotification(notificationItem)

        coVerify {
            notificationsDao.insertNotification(
                NotificationItemEntity(
                    null,
                    notificationItem.firedAtMillis,
                    notificationItem.airingScheduleItem.id
                )
            )
        }

        cleanupMocks()
    }

    @Test
    fun getPendingNotifications() = runTest {
        initMocks(testScheduler)

        val pendingNotifications = persistentMediaDatabaseExecutor.getPendingNotifications()

        assertThat(pendingNotifications).isEqualTo(pendingFlowData)

        coVerify {
            notificationsDao.getPending(Int.MAX_VALUE)
        }

        cleanupMocks()
    }
}