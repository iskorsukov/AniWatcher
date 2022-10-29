package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDaoTest {

    lateinit var mediaDao: MediaDao
    lateinit var mediaDatabase: MediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mediaDatabase = Room.inMemoryDatabaseBuilder(context, MediaDatabase::class.java).build()
        mediaDao = mediaDatabase.mediaDao()
    }

    @After
    fun closeDb() {
        mediaDatabase.close()
    }

    @Test
    fun insertMedia() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(emptyList())
        )
    }

    @Test
    fun insertMediaAndAiringSchedules() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))
        mediaDao.insertSchedules(entity.airingScheduleEntityList)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(entity.airingScheduleEntityList)
        )
    }

    @Test
    fun followMedia() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))
        mediaDao.followMedia(EntityTestDataCreator.baseFollowingEntity())

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(emptyList())
                .followingEntity(EntityTestDataCreator.baseFollowingEntity())
        )
    }

    @Test
    fun unfollowMedia() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))
        mediaDao.followMedia(EntityTestDataCreator.baseFollowingEntity())
        mediaDao.unfollowMedia(entity.mediaItemEntity.mediaId)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(emptyList())
        )
    }

    @Test
    fun followMedia_changesFlow() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))

        var outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(emptyList())
        )

        mediaDao.followMedia(EntityTestDataCreator.baseFollowingEntity())

        outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(emptyList())
                .followingEntity(EntityTestDataCreator.baseFollowingEntity())
        )
    }

    @Test
    fun clearMedia() = runBlocking {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity

        mediaDao.insertMedia(listOf(entity.mediaItemEntity))
        mediaDao.insertSchedules(entity.airingScheduleEntityList)
        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()
        // clearing media cascades to airing schedules
        val airingCursor = mediaDatabase.query("SELECT * FROM airing", null)

        assertThat(outEntity).isEmpty()
        assertThat(airingCursor.count).isEqualTo(0)
    }
}