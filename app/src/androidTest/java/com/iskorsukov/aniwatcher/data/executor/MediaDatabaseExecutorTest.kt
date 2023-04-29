package com.iskorsukov.aniwatcher.data.executor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.room.MediaDao
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.settings.*
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDatabaseExecutorTest {

    private lateinit var mediaDao: MediaDao
    private lateinit var mediaDatabase: MediaDatabase

    private lateinit var mediaDatabaseExecutor: MediaDatabaseExecutor

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mediaDatabase = spyk(
            Room
                .inMemoryDatabaseBuilder(context, MediaDatabase::class.java)
                .build()
        )
        mediaDao = mediaDatabase.mediaDao()
        every { mediaDatabase.mediaDao() } returns mediaDao

        mediaDatabaseExecutor = MediaDatabaseExecutor(
            mediaDatabase
        )
    }

    @After
    fun tearDown() {
        mediaDatabase.close()
    }

    @Test
    fun mediaDataFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val entity = mediaDatabaseExecutor.mediaDataFlow.first()

        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun mediaDataFlow_emptySchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val entity = mediaDatabaseExecutor.mediaDataFlow.first()

        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values).containsExactly(emptyList<AiringScheduleEntity>())
    }

    @Test
    fun getMediaWithAiringSchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedules(mediaItemEntity.mediaId)
            .first()

        assertThat(entity).isNotNull()
        assertThat(entity!!.first).isEqualTo(mediaItemEntity)
        assertThat(entity.second).containsExactlyElementsIn(airingScheduleEntityList)
    }

    @Test
    fun getMediaWithAiringSchedules_notFound(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
        val missingId = 1337

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedules(missingId)
            .first()

        assertThat(entity).isNull()
    }

    @Test
    fun updateMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        val entityMap = mapOf(
            mediaItemEntity to airingScheduleEntityList
        )

        mediaDatabaseExecutor.updateMedia(entityMap)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }
}