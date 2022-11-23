package com.iskorsukov.aniwatcher.domain.notification

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
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

    private val mediaDatabaseExecutor: MediaDatabaseExecutor =
        mockk<MediaDatabaseExecutor>(relaxed = true).apply {
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
    private val notificationsRepository = NotificationsRepositoryImpl(mediaDatabaseExecutor)

    @Test
    fun notificationsFlow() = runTest {
        val data = notificationsRepository.notificationsFlow.first()
        assertThat(data).containsExactly(ModelTestDataCreator.baseNotificationItem())
    }

    @Test
    fun saveNotification() = runTest {
        val notificationItem = ModelTestDataCreator.baseNotificationItem()

        notificationsRepository.saveNotification(notificationItem)

        coVerify { mediaDatabaseExecutor.saveNotification(notificationItem) }
    }
}