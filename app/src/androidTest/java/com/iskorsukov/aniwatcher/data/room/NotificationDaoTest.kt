package com.iskorsukov.aniwatcher.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {

    private lateinit var persistentMediaDao: PersistentMediaDao
    private lateinit var notificationsDao: NotificationsDao
    private lateinit var persistentMediaDatabase: PersistentMediaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        persistentMediaDatabase = Room
            .inMemoryDatabaseBuilder(context, PersistentMediaDatabase::class.java)
            .build()
        persistentMediaDao = persistentMediaDatabase.persistentMediaDao()
        notificationsDao = persistentMediaDatabase.notificationsDao()
    }

    @After
    fun closeDb() {
        persistentMediaDatabase.close()
    }

    @Test
    fun getAll(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)
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

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        notificationsDao.insertNotification(notificationEntity)

        val outEntity = persistentMediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )

        val mediaWithAiringSchedulesAndNotificationsEntity = notificationsDao.getAll().first()

        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.size).isEqualTo(1)
        assertThat(mediaWithAiringSchedulesAndNotificationsEntity.keys).containsExactly(mediaItemEntity)
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
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val pendingScheduleEntityMap = notificationsDao.getPending(Int.MAX_VALUE)

        assertThat(pendingScheduleEntityMap.size).isEqualTo(1)
        assertThat(pendingScheduleEntityMap.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun getPending_ignoresFired(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationEntity)

        val pendingScheduleEntityMap = notificationsDao.getPending(0)

        assertThat(pendingScheduleEntityMap.size).isEqualTo(0)
    }
}