package com.iskorsukov.aniwatcher.domain.notification

import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.executor.PersistentMediaDatabaseExecutor
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsRepositoryTest {

    private val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(mediaId = 1)
    private val airingScheduleEntityFirst = EntityTestDataCreator.airingScheduleEntity(
        airingScheduleEntityId = 1,
        mediaItemRelationId = 1
    )
    private val notificationItemEntityFirst = EntityTestDataCreator.notificationItemEntity(
        notificationItemId = 1,
        airingScheduleItemRelationId = 1
    )
    private val airingScheduleEntitySecond = EntityTestDataCreator.airingScheduleEntity(
        airingScheduleEntityId = 2,
        mediaItemRelationId = 1
    )
    private val notificationItemEntitySecond = EntityTestDataCreator.notificationItemEntity(
        notificationItemId = 2,
        firedAtMillis = 100L,
        airingScheduleItemRelationId = 2
    )
    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor =
        mockk<PersistentMediaDatabaseExecutor>(relaxed = true).apply {
            coEvery { notificationsFlow } returns flowOf(
                mapOf(
                    Pair(
                        mediaItemEntity,
                        listOf(
                            AiringScheduleAndNotificationEntity(
                                airingScheduleEntityFirst,
                                notificationItemEntityFirst
                            ),
                            AiringScheduleAndNotificationEntity(
                                airingScheduleEntitySecond,
                                notificationItemEntitySecond
                            )
                        )
                    )
                )
            )
        }

    private val sharedPreferencesEditor: SharedPreferences.Editor = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk<SharedPreferences>(relaxed = true).apply {
        coEvery { edit() } returns sharedPreferencesEditor
        coEvery { getInt(NotificationsRepositoryImpl.UNREAD_NOTIFICATIONS_KEY, any()) } returns 2
    }
    private val notificationsRepository = NotificationsRepositoryImpl(
        persistentMediaDatabaseExecutor,
        sharedPreferences
    )

    @Test
    fun notificationsFlow_sortsByFiredAtDesc() = runTest {
        val data = notificationsRepository.notificationsFlow.first()
        assertThat(data).containsExactly(
            NotificationItem.fromEntity(
                mediaItemEntity,
                AiringScheduleAndNotificationEntity(
                    airingScheduleEntitySecond,
                    notificationItemEntitySecond)
            ),
            NotificationItem.fromEntity(
                mediaItemEntity,
                AiringScheduleAndNotificationEntity(
                    airingScheduleEntityFirst,
                    notificationItemEntityFirst)
            )
        )
    }

    @Test
    fun increaseUnreadNotificationsCounter() = runTest {
        notificationsRepository.increaseUnreadNotificationsCounter()

        assertThat(notificationsRepository.unreadNotificationsCounterStateFlow.first())
            .isEqualTo(3)
        coVerify {
            sharedPreferencesEditor.putInt(NotificationsRepositoryImpl.UNREAD_NOTIFICATIONS_KEY, 3)
        }
    }

    @Test
    fun resetUnreadNotificationsCounter() = runTest {
        notificationsRepository.resetUnreadNotificationsCounter()

        assertThat(notificationsRepository.unreadNotificationsCounterStateFlow.first())
            .isEqualTo(0)
        coVerify {
            sharedPreferencesEditor.putInt(NotificationsRepositoryImpl.UNREAD_NOTIFICATIONS_KEY, 0)
        }
    }
}