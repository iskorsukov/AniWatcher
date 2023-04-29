package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistentMediaDaoTest {

    private lateinit var persistentMediaDao: PersistentMediaDao
    private lateinit var persistentMediaDatabase: PersistentMediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        persistentMediaDatabase = Room
            .inMemoryDatabaseBuilder(context, PersistentMediaDatabase::class.java)
            .build()
        persistentMediaDao = persistentMediaDatabase.persistentMediaDao()
    }

    @After
    fun closeDb() {
        persistentMediaDatabase.close()
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

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val followingMediaEntitiesMap = persistentMediaDao.getAll().first()

        assertThat(followingMediaEntitiesMap).isNotEmpty()
        assertThat(followingMediaEntitiesMap.keys).containsExactly(mediaItemEntity)
        assertThat(followingMediaEntitiesMap.values.flatten()).containsExactlyElementsIn(airingScheduleEntityList)
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

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val followingMediaEntitiesMap = persistentMediaDao.getById(mediaItemEntity.mediaId).first()

        assertThat(followingMediaEntitiesMap).isNotEmpty()
        assertThat(followingMediaEntitiesMap.keys).containsExactly(mediaItemEntity)
        assertThat(followingMediaEntitiesMap.values.flatten()).containsExactlyElementsIn(airingScheduleEntityList)
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

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val followingMediaEntitiesMap = persistentMediaDao.getById(missingId).first()

        assertThat(followingMediaEntitiesMap).isEmpty()
    }

    @Test
    fun insertMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)

        persistentMediaDao.insertMedia(mediaItemEntity)

        val followingMediaEntitiesMap = persistentMediaDao.getById(mediaItemEntity.mediaId).first()

        assertThat(followingMediaEntitiesMap).isNotEmpty()
        assertThat(followingMediaEntitiesMap.keys).containsExactly(mediaItemEntity)
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

        persistentMediaDao.insertMedia(mediaItemEntity)

        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val followingMediaEntitiesMap = persistentMediaDao.getById(mediaItemEntity.mediaId).first()

        assertThat(followingMediaEntitiesMap).isNotEmpty()
        assertThat(followingMediaEntitiesMap.values.flatten()).containsExactlyElementsIn(airingScheduleEntityList)
    }

    @Test
    fun deleteMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        persistentMediaDao.deleteMedia(mediaItemEntity.mediaId)

        val followingMediaEntitiesMap = persistentMediaDao.getById(mediaItemEntity.mediaId).first()

        assertThat(followingMediaEntitiesMap).isEmpty()
    }
}