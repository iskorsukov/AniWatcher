package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.test.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDaoTest {

    lateinit var mediaDao: MediaDao
    lateinit var notificationsDao: NotificationsDao
    lateinit var mediaDatabase: MediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mediaDatabase = Room.inMemoryDatabaseBuilder(context, MediaDatabase::class.java).build()
        mediaDao = mediaDatabase.mediaDao()
        notificationsDao = mediaDatabase.notificationsDao()
    }

    @After
    fun closeDb() {
        mediaDatabase.close()
    }

    @Test
    fun getAll(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                EntityTestDataCreator.baseMediaItemEntity(),
                null
            )
        )
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun getAll_emptySchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                EntityTestDataCreator.baseMediaItemEntity(),
                null
            )
        )
        assertThat(outEntity.values.flatten()).isEmpty()
    }

    @Test
    fun getById(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getById(1).first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                EntityTestDataCreator.baseMediaItemEntity(),
                null
            )
        )
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun getById_emptySchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val outEntity = mediaDao.getById(1).first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                EntityTestDataCreator.baseMediaItemEntity(),
                null
            )
        )
        assertThat(outEntity.values.flatten()).isEmpty()
    }

    @Test
    fun insertMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                EntityTestDataCreator.baseMediaItemEntity(),
                null
            )
        )
        assertThat(outEntity.values).containsExactly(
            emptyList<AiringScheduleEntity>()
        )
    }

    @Test
    fun insertSchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        mediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun followMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        mediaDao.followMedia(followingEntity)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                followingEntity
            )
        )
    }

    @Test
    fun followMedia_changesFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        var outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(outEntity.values).containsExactly(
            emptyList<AiringScheduleEntity>()
        )

        mediaDao.followMedia(followingEntity)

        outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                followingEntity
            )
        )
    }

    @Test
    fun unfollowMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.followMedia(followingEntity)

        mediaDao.unfollowMedia(followingEntity.mediaItemRelationId)

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
    }

    @Test
    fun unfollowMedia_list(): Unit = runBlocking {
        val mediaItemEntityList = listOf(
            EntityTestDataCreator.baseMediaItemEntity(),
            EntityTestDataCreator.baseMediaItemEntity().mediaId(2)
        )
        val followingEntityList = listOf(
            EntityTestDataCreator.baseFollowingEntity(),
            EntityTestDataCreator.baseFollowingEntity()
                .followingEntryId(2)
                .mediaItemRelationId(2)
        )

        mediaDao.insertMedia(mediaItemEntityList)
        mediaDao.followMedia(followingEntityList[0])
        mediaDao.followMedia(followingEntityList[1])

        mediaDao.unfollowMedia(listOf(1, 2))

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(2)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntityList[0],
                null
            ),
            MediaItemAndFollowingEntity(
                mediaItemEntityList[1],
                null
            )
        )
    }

    @Test
    fun clearMedia() = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity).isEmpty()
    }

    @Test
    fun clearMedia_cascadesToAiringSchedules() = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()

        val airingCursor = mediaDatabase.query("SELECT * FROM airing", null)
        assertThat(outEntity).isEmpty()
        assertThat(airingCursor.count).isEqualTo(0)
    }

    @Test
    fun clearMedia_ignoresFollowedMedia() = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)
        mediaDao.followMedia(followingEntity)

        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()

        val airingCursor = mediaDatabase.query("SELECT * FROM airing", null)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                followingEntity
            )
        )
        assertThat(airingCursor.count).isEqualTo(airingScheduleEntityList.size)
    }

    @Test
    fun clearMedia_cascadesToNotifications() = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()
        val notificationItemEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationItemEntity)

        mediaDao.clearMedia()

        val outEntity = mediaDao.getAll().first()

        val notificationsCursor = mediaDatabase.query("SELECT * FROM notifications", null)
        assertThat(outEntity.keys).isEmpty()
        assertThat(notificationsCursor.count).isEqualTo(0)
    }
}