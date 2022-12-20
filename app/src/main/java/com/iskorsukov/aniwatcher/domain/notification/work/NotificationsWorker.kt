package com.iskorsukov.aniwatcher.domain.notification.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsInteractor
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that checks if there are pending notifications and fires them
 *
 * @property clock a clock for setting firedAt time on notification entities
 * @property notificationsRepository repository for getting pending notifications and saving fired ones
 * @property notificationsInteractor interactor for firing notifications
 * @constructor
 *
 * @param appContext context
 * @param workerParams worker params
 */
@HiltWorker
class NotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val clock: LocalClockSystem,
    private val notificationsRepository: NotificationsRepository,
    private val notificationsInteractor: NotificationsInteractor
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        notificationsInteractor.createNotificationsChannel()
        val pendingNotifications = notificationsRepository.getPendingSchedulesToNotify()
        notificationsInteractor.fireAiredNotifications(pendingNotifications)
        saveNotifications(pendingNotifications)
        return Result.success()
    }

    private suspend fun saveNotifications(airingScheduleItemList: List<AiringScheduleItem>) {
        airingScheduleItemList.forEach { airingScheduleItem ->
            notificationsRepository.saveNotification(
                NotificationItem(
                    airingScheduleItem = airingScheduleItem,
                    firedAtMillis = clock.currentTimeMillis()
                )
            )
            notificationsRepository.increaseUnreadNotificationsCounter()
        }
    }

}