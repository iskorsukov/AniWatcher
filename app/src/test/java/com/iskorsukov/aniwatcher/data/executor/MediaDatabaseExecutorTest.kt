package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.data.room.MediaDao
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.data.room.NotificationsDao
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
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
class MediaDatabaseExecutorTest {

    private val mediaDao: MediaDao = mockk(relaxed = true)
    private val notificationsDao: NotificationsDao = mockk(relaxed = true)
    private val mediaDatabase: MediaDatabase = mockk(relaxed = true)

    private lateinit var mediaDatabaseExecutor: MediaDatabaseExecutor

    private val mediaFlowData = mapOf(
        MediaItemAndFollowingEntity(
            EntityTestDataCreator.baseMediaItemEntity(),
            null
        ) to EntityTestDataCreator.baseAiringScheduleEntityList()
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

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")

        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        every { mediaDatabase.mediaDao() } returns mediaDao
        every { mediaDao.getAll() } returns flowOf(mediaFlowData)
        every { mediaDao.getById(any()) } returns flowOf(mediaFlowData)

        every { mediaDatabase.notificationsDao() } returns notificationsDao
        every { notificationsDao.getAll() } returns flowOf(notificationFlowData)

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { mediaDatabase.withTransaction(capture(transactionLambda)) } coAnswers  {
            transactionLambda.captured.invoke()
        }

        mediaDatabaseExecutor = MediaDatabaseExecutor(mediaDatabase)
    }

    private fun cleanupMocks() {
        unmockkObject(DispatcherProvider)
    }

    @Test
    fun mediaDataFlow() = runTest {
        initMocks(testScheduler)

        val entity = mediaDatabaseExecutor.mediaDataFlow.first()

        assertThat(entity)
            .isEqualTo(mediaFlowData)

        coVerify { mediaDao.getAll() }

        cleanupMocks()
    }

    @Test
    fun notificationsFlow() = runTest {
        initMocks(testScheduler)

        val entity = mediaDatabaseExecutor.notificationsFlow.first()

        assertThat(entity).isEqualTo(notificationFlowData)

        coVerify { notificationsDao.getAll() }

        cleanupMocks()
    }

    @Test
    fun getMediaWithAiringSchedulesAndFollowing() = runTest {
        initMocks(testScheduler)

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedulesAndFollowing(1)
            .first()

        assertThat(entity)
            .isEqualTo(mediaFlowData)

        coVerify {
            mediaDao.getById(1)
        }

        cleanupMocks()
    }

    @Test
    fun updateMedia() = runTest {
        initMocks(testScheduler)

        val entityMap = mapOf(
            EntityTestDataCreator.baseMediaItemEntity() to
                    EntityTestDataCreator.baseAiringScheduleEntityList()
        )

        mediaDatabaseExecutor.updateMedia(entityMap)
        advanceUntilIdle()

        coVerify {
            mediaDao.clearMedia()
            mediaDao.insertMedia(entityMap.keys.toList())
            mediaDao.insertSchedules(entityMap.values.flatten())
        }

        cleanupMocks()
    }

    @Test
    fun followMedia() = runTest {
        initMocks(testScheduler)

        mediaDatabaseExecutor.followMedia(1)
        advanceUntilIdle()

        coVerify {
            mediaDao.followMedia(FollowingEntity(null, 1))
        }

        cleanupMocks()
    }

    @Test
    fun unfollowMedia() = runTest {
        initMocks(testScheduler)

        mediaDatabaseExecutor.unfollowMedia(1)
        advanceUntilIdle()

        coVerify {
            mediaDao.unfollowMedia(1)
        }

        cleanupMocks()
    }

    @Test
    fun clearAiredSchedules() = runTest {
        initMocks(testScheduler)

        mediaDatabaseExecutor.clearAiredSchedules()

        coVerify { mediaDao.clearAiredSchedules() }

        cleanupMocks()
    }
}