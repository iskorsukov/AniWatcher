package com.iskorsukov.aniwatcher.data.executor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.room.*
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.*
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistentMediaDatabaseExecutorTest {

    private lateinit var persistentMediaDao: PersistentMediaDao
    private lateinit var notificationsDao: NotificationsDao
    private lateinit var persistentMediaDatabase: PersistentMediaDatabase

    private lateinit var clock: LocalClockSystem

    private lateinit var persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        persistentMediaDatabase = spyk(
            Room
                .inMemoryDatabaseBuilder(context, PersistentMediaDatabase::class.java)
                .build()
        )
        persistentMediaDao = persistentMediaDatabase.persistentMediaDao()
        every { persistentMediaDatabase.persistentMediaDao() } returns persistentMediaDao
        notificationsDao = persistentMediaDatabase.notificationsDao()
        every { persistentMediaDatabase.notificationsDao() } returns notificationsDao

        clock = mockk<LocalClockSystem>(relaxed = true).apply {
            coEvery { currentTimeSeconds() } returns 0
        }

        persistentMediaDatabaseExecutor = PersistentMediaDatabaseExecutor(
            persistentMediaDatabase,
            clock
        )
    }

    @After
    fun tearDown() {
        persistentMediaDatabase.close()
    }

    @Test
    fun followedMediaFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        val outEntity = persistentMediaDatabaseExecutor.followedMediaFlow.first()

        assertThat(outEntity).isNotEmpty()
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
        assertThat(outEntity.values.flatten()).containsExactlyElementsIn(airingScheduleEntityList)
    }

    @Test
    fun saveMediaWithSchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDatabaseExecutor.saveMediaWithSchedules(
            mediaItemEntity to airingScheduleEntityList
        )

        val outEntity = persistentMediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(mediaItemEntity)
    }

    @Test
    fun unfollowMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        persistentMediaDatabaseExecutor.deleteMedia(mediaItemEntity.mediaId)

        val outEntity = persistentMediaDao.getAll().first()

        assertThat(outEntity.size).isEqualTo(0)
    }

    @Test
    fun notificationsFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
        val notificationEntity = EntityTestDataCreator.notificationItemEntity(
            notificationItemId = 1,
            airingScheduleItemRelationId = 1
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationEntity)

        val entity = persistentMediaDatabaseExecutor.notificationsFlow.first()

        assertThat(entity).isEqualTo(
            mapOf(
                mediaItemEntity to listOf(
                    AiringScheduleAndNotificationEntity(
                        airingScheduleEntityList.first(),
                        notificationEntity
                    )
                )
            )
        )
        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                airingScheduleEntityList.first(),
                notificationEntity
            )
        )
    }

    @Test
    fun saveNotification(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
        val notificationEntity = EntityTestDataCreator.notificationItemEntity(
            notificationItemId = 1,
            airingScheduleItemRelationId = 1
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        persistentMediaDatabaseExecutor.saveNotification(
            ModelTestDataCreator.notificationItem(
                null,
                airingScheduleItem = AiringScheduleItem.fromEntity(airingScheduleEntityList.first()),
                mediaItem = MediaItem.fromEntity(mediaItemEntity, true)
            )
        )

        val entity = persistentMediaDatabaseExecutor.notificationsFlow.first()

        assertThat(entity).isEqualTo(
            mapOf(
                mediaItemEntity to listOf(
                    AiringScheduleAndNotificationEntity(
                        airingScheduleEntityList.first(),
                        notificationEntity
                    )
                )
            )
        )
        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                airingScheduleEntityList.first(),
                notificationEntity
            )
        )
    }

    @Test
    fun getPendingNotifications(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        coEvery { clock.currentTimeSeconds() } returns Int.MAX_VALUE
        val entity = persistentMediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactly(airingScheduleEntityList.first())
    }

    @Test
    fun getPendingNotifications_ignoresNotAired(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)

        coEvery { clock.currentTimeSeconds() } returns 0
        val entity = persistentMediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity).isEmpty()
    }

    @Test
    fun getPendingNotifications_ignoresSaved(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
        val airingScheduleEntityList = listOf(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            )
        )
        val notificationEntity = EntityTestDataCreator.notificationItemEntity(
            airingScheduleItemRelationId = 1
        )

        persistentMediaDao.insertMedia(mediaItemEntity)
        persistentMediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationEntity)

        coEvery { clock.currentTimeSeconds() } returns Int.MAX_VALUE
        val entity = persistentMediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity).isEmpty()
    }
}