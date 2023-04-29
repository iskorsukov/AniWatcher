package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.room.MediaDao
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
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
    private val mediaDatabase: MediaDatabase = mockk(relaxed = true)

    private lateinit var mediaDatabaseExecutor: MediaDatabaseExecutor

    private val mediaFlowPair = Pair(
        EntityTestDataCreator.mediaItemEntity(mediaId = 1),
        listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
    )
    private val mediaFlowData = mapOf(mediaFlowPair)

    private fun initMocks(testScheduler: TestCoroutineScheduler) {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")

        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        every { mediaDatabase.mediaDao() } returns mediaDao
        every { mediaDao.getAll() } returns flowOf(mediaFlowData)
        every { mediaDao.getById(any()) } returns flowOf(mediaFlowData)

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
    fun getMediaWithAiringSchedules() = runTest {
        initMocks(testScheduler)

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedules(1)
            .first()

        assertThat(entity)
            .isEqualTo(mediaFlowPair)

        coVerify {
            mediaDao.getById(1)
        }

        cleanupMocks()
    }

    @Test
    fun updateMedia() = runTest {
        initMocks(testScheduler)

        mediaDatabaseExecutor.updateMedia(mediaFlowData)
        advanceUntilIdle()

        coVerifyOrder {
            mediaDao.clearAiringSchedules()
            mediaDao.clearMedia()
            mediaDao.insertMedia(mediaFlowData.keys.toList())
            mediaDao.insertSchedules(mediaFlowData.values.flatten())
        }

        cleanupMocks()
    }
}