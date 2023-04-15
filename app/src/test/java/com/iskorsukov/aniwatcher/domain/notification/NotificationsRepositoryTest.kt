package com.iskorsukov.aniwatcher.domain.notification

import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.executor.PersistentMediaDatabaseExecutor
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
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

    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor =
        mockk<PersistentMediaDatabaseExecutor>(relaxed = true).apply {
            coEvery { notificationsFlow } returns flowOf(
                mapOf(
                    EntityTestDataCreator.baseMediaItemEntity() to
                            listOf(
                                AiringScheduleAndNotificationEntity(
                                    EntityTestDataCreator.baseAiringScheduleEntity(),
                                    EntityTestDataCreator.baseNotificationEntity()
                                )
                            )
                )
            )
        }

    private val sharedPreferencesEditor: SharedPreferences.Editor = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk<SharedPreferences>(relaxed = true).apply {
        coEvery { edit() } returns sharedPreferencesEditor
        coEvery { getInt(any(), any()) } returns 1
    }
    private val notificationsRepository = NotificationsRepositoryImpl(
        persistentMediaDatabaseExecutor,
        sharedPreferences
    )

    @Test
    fun notificationsFlow() = runTest {
        val data = notificationsRepository.notificationsFlow.first()
        assertThat(data).containsExactly(ModelTestDataCreator.baseNotificationItem())
    }

    @Test
    fun unreadNotificationsCounterFlow() = runTest {
        val count = notificationsRepository.unreadNotificationsCounterStateFlow.first()
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun saveNotification() = runTest {
        val notificationItem = ModelTestDataCreator.baseNotificationItem()

        notificationsRepository.saveNotification(notificationItem)

        coVerify { persistentMediaDatabaseExecutor.saveNotification(notificationItem) }
    }

    @Test
    fun increaseUnreadNotificationsCounter() = runTest {
        notificationsRepository.increaseUnreadNotificationsCounter()

        assertThat(notificationsRepository.unreadNotificationsCounterStateFlow.first())
            .isEqualTo(2)
        coVerify {
            sharedPreferencesEditor.putInt(any(), 2)
        }
    }

    @Test
    fun resetUnreadNotificationsCounter() = runTest {
        notificationsRepository.resetUnreadNotificationsCounter()

        assertThat(notificationsRepository.unreadNotificationsCounterStateFlow.first())
            .isEqualTo(0)
        coVerify {
            sharedPreferencesEditor.putInt(any(), 0)
        }
    }
}