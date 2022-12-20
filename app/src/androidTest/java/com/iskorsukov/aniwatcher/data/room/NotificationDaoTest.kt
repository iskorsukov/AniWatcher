package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.combined.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {

    private lateinit var notificationsDao: NotificationsDao
    private lateinit var mediaDao: MediaDao
    private lateinit var mediaDatabase: MediaDatabase

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
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationEntity)

        val mediaWithAiringSchedulesAndNotificationsEntity = notificationsDao.getAll().first()

        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.size).isEqualTo(1)
        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.keys).containsExactly(
            mediaItemEntity
        )
        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                EntityTestDataCreator.baseAiringScheduleEntity(),
                notificationEntity
            )
        )
    }

    @Test
    fun insertNotification(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        notificationsDao.insertNotification(notificationEntity)

        val outEntity = mediaDao.getAllNotAired(0).first()

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

        val mediaWithAiringSchedulesAndNotificationsEntity = notificationsDao.getAll().first()

        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.size).isEqualTo(1)
        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.keys).containsExactly(
            mediaItemEntity
        )
        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                EntityTestDataCreator.baseAiringScheduleEntity(),
                notificationEntity
            )
        )
    }

    @Test
    fun getPending(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)

        val pendingScheduleEntityMap = notificationsDao.getPending(Int.MAX_VALUE)

        assertThat(pendingScheduleEntityMap.size).isEqualTo(1)
        assertThat(pendingScheduleEntityMap.values.flatten()).containsExactly(
            airingScheduleEntity
        )
    }

    @Test
    fun getPending_ignoresFired(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)
        notificationsDao.insertNotification(notificationEntity)

        val pendingScheduleEntityMap = notificationsDao.getPending(Int.MAX_VALUE)

        assertThat(pendingScheduleEntityMap.size).isEqualTo(0)
    }
}