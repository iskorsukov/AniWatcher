package com.iskorsukov.aniwatcher.data.executor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.combined.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.data.room.MediaDao
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.data.room.NotificationsDao
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
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
    private lateinit var notificationsDao: NotificationsDao
    private lateinit var mediaDatabase: MediaDatabase

    private lateinit var clock: LocalClockSystem

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
        notificationsDao = mediaDatabase.notificationsDao()
        every { mediaDatabase.notificationsDao() } returns notificationsDao

        clock = mockk<LocalClockSystem>(relaxed = true).apply {
            coEvery { currentTimeSeconds() } returns 0
        }

        mediaDatabaseExecutor = MediaDatabaseExecutor(
            mediaDatabase,
            clock
        )
    }

    @After
    fun tearDown() {
        mediaDatabase.close()
    }

    @Test
    fun mediaDataFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)

        val entity = mediaDatabaseExecutor.mediaDataFlow.first()

        assertThat(entity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(entity.values.flatten()).containsExactlyElementsIn(
            airingScheduleEntityList
        )
    }

    @Test
    fun mediaDataFlow_emptySchedules(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val entity = mediaDatabaseExecutor.mediaDataFlow.first()

        assertThat(entity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(entity.values).containsExactly(emptyList<AiringScheduleEntity>())
    }

    @Test
    fun notificationsFlow(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(airingScheduleEntityList)
        notificationsDao.insertNotification(notificationEntity)

        val entity = mediaDatabaseExecutor.notificationsFlow.first()

        assertThat(entity).isEqualTo(
            mapOf(
                mediaItemEntity to listOf(
                    AiringScheduleAndNotificationEntity(
                        EntityTestDataCreator.baseAiringScheduleEntity(),
                        notificationEntity
                    )
                )
            )
        )
        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactly(
            AiringScheduleAndNotificationEntity(
                EntityTestDataCreator.baseAiringScheduleEntity(),
                notificationEntity
            )
        )
    }

    @Test
    fun getPendingNotifications(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)

        coEvery { clock.currentTimeSeconds() } returns Int.MAX_VALUE
        val entity = mediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity.keys).containsExactly(mediaItemEntity)
        assertThat(entity.values.flatten()).containsExactly(airingScheduleEntity)
    }

    @Test
    fun getPendingNotifications_ignoresNotAired(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)

        coEvery { clock.currentTimeSeconds() } returns 0
        val entity = mediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity).isEmpty()
    }

    @Test
    fun getPendingNotifications_ignoresSaved(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)
        notificationsDao.insertNotification(notificationEntity)

        coEvery { clock.currentTimeSeconds() } returns Int.MAX_VALUE
        val entity = mediaDatabaseExecutor.getPendingNotifications()

        assertThat(entity).isEmpty()
    }

    @Test
    fun getMediaWithAiringSchedulesAndFollowing(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.followMedia(followingEntity)

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedulesAndFollowing(1)
            .first()

        assertThat(entity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                followingEntity
            )
        )
        assertThat(entity.values).containsExactly(emptyList<AiringScheduleEntity>())
    }

    @Test
    fun getMediaWithAiringSchedulesAndFollowing_noFollowing(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))

        val entity = mediaDatabaseExecutor
            .getMediaWithAiringSchedulesAndFollowing(1)
            .first()

        assertThat(entity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(entity.values).containsExactly(emptyList<AiringScheduleEntity>())
    }

    @Test
    fun updateMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val airingScheduleEntityList = EntityTestDataCreator.baseAiringScheduleEntityList()

        val entityMap = mapOf(
            mediaItemEntity to airingScheduleEntityList
        )

        mediaDatabaseExecutor.updateMedia(entityMap)

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
    }

    @Test
    fun followMedia(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))

        mediaDatabaseExecutor.followMedia(1)

        val outEntity = mediaDao.getAllNotAired(0).first()

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

        mediaDatabaseExecutor.unfollowMedia(1)

        val outEntity = mediaDao.getAllNotAired(0).first()

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
    }

    @Test
    fun unfollowMedia_clearsNotifications(): Unit = runBlocking {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()
        val airingScheduleEntity = EntityTestDataCreator.baseAiringScheduleEntity()
        val notificationEntity = EntityTestDataCreator.baseNotificationEntity()

        mediaDao.insertMedia(listOf(mediaItemEntity))
        mediaDao.insertSchedules(listOf(airingScheduleEntity))
        mediaDao.followMedia(followingEntity)
        notificationsDao.insertNotification(notificationEntity)

        mediaDatabaseExecutor.unfollowMedia(1)

        val outEntity = mediaDao.getAllNotAired(0).first()
        val notificationsCursor = mediaDatabase.query("SELECT * FROM notifications", null)

        assertThat(outEntity.size).isEqualTo(1)
        assertThat(outEntity.keys).containsExactly(
            MediaItemAndFollowingEntity(
                mediaItemEntity,
                null
            )
        )
        assertThat(notificationsCursor.count).isEqualTo(0)
    }
}