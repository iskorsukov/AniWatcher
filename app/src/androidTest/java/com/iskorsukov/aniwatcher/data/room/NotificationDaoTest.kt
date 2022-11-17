package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleWithNotificationEntity
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.airingScheduleEntityList
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
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
            .mediaItemWithAiringSchedulesEntity
        mediaDao.insertMedia(listOf(entity.mediaItemEntity))
        mediaDao.insertSchedules(entity.airingScheduleEntityList)

        notificationsDao.insertNotification(EntityTestDataCreator.baseNotificationEntity())

        val outEntity = mediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .airingScheduleEntityList(entity.airingScheduleEntityList)
        )

        val notificationEntity = notificationsDao.getAll().first()

        assertThat(notificationEntity.size).isEqualTo(1)
        assertThat(notificationEntity.keys).containsExactly(
            EntityTestDataCreator.baseMediaItemEntity()
        )
        assertThat(notificationEntity.values).containsExactly(
            AiringScheduleWithNotificationEntity(
                EntityTestDataCreator.baseAiringScheduleEntity(),
                EntityTestDataCreator.baseNotificationEntity()
            )
        )
    }
}