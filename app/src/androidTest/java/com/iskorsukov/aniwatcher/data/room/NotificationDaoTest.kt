package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemAndFollowingEntity
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
    fun insertNotification(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        notificationsDao.insertNotification(EntityTestDataCreator.baseNotificationEntity())

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

        val notificationEntity = notificationsDao.getAll().first()

        assertThat(notificationEntity.size).isEqualTo(1)
        assertThat(notificationEntity.keys).containsExactly(
            EntityTestDataCreator.baseMediaItemEntity()
        )
        assertThat(notificationEntity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                EntityTestDataCreator.baseAiringScheduleEntity(),
                EntityTestDataCreator.baseNotificationEntity()
            )
        )
    }
}