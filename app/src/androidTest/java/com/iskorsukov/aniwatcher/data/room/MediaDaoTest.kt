package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.test.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDaoTest {

    private lateinit var mediaDao: MediaDao
    private lateinit var mediaDatabase: MediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mediaDatabase = Room
            .inMemoryDatabaseBuilder(context, MediaDatabase::class.java)
            .build()
        mediaDao = mediaDatabase.mediaDao()
    }

    @After
    fun closeDb() {
        mediaDatabase.close()
    }

    @Test
    fun getAll(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity).isNotEmpty()
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun getById(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getById(mediaItemEntity.mediaId).first()

        assertThat(outEntity).isNotEmpty()
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun getById_notFound(): Unit = runBlocking {
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

        val outEntity = mediaDao.getById(missingId).first()

        assertThat(outEntity).isEmpty()
    }

    @Test
    fun insertMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity).isNotEmpty()
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values).containsExactly(
            emptyList<AiringScheduleEntity>()
        )
    }

    @Test
    fun insertSchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))

        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity).isNotEmpty()
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun clearMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)

        mediaDao.insertMedia(listOf(mediaItemEntity))

        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity).isEmpty()
    }

    @Test
    fun clearAiringSchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        mediaDao.clearAiringSchedules()

        val airingCursor = mediaDatabase.query("SELECT * FROM airing", null)
        assertThat(airingCursor.count).isEqualTo(0)
    }
}