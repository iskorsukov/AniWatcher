package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.room.MediaDao
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaDatabaseExecutorTest {

    private val mediaDao: MediaDao = mockk(relaxed = true)
    private val mediaDatabase: MediaDatabase = mockk(relaxed = true)

    private lateinit var mediaDatabaseExecutor: MediaDatabaseExecutor

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")

        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        every { mediaDatabase.mediaDao() } returns mediaDao

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
    fun updateMedia() = runTest {
        initMocks(testScheduler)

        val entityList = listOf(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .mediaItemWithAiringSchedulesEntity
        )

        mediaDatabaseExecutor.updateMedia(entityList)
        advanceUntilIdle()

        coVerify {
            mediaDao.clearMedia()
            mediaDao.insertMedia(listOf(entityList[0].mediaItemEntity))
            mediaDao.insertSchedules(entityList[0].airingScheduleEntityList)
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
}