package com.iskorsukov.aniwatcher.domain.notification.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.notification.work.util.NotificationBuilderHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationsRepository: NotificationsRepository,
    private val notificationManagerCompat: NotificationManagerCompat
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        createNotificationChannel()
        notificationsRepository.getPendingSchedulesToNotify().let {
            it.forEach {
                fireAiredNotification(it)
            }
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    private suspend fun fireAiredNotification(airingScheduleItem: AiringScheduleItem) {
        val notification = NotificationBuilderHelper.buildNotification(applicationContext, airingScheduleItem)
        notificationManagerCompat.notify(airingScheduleItem.id, notification)
        notificationsRepository.saveNotification(
            NotificationItem(
                airingScheduleItem = airingScheduleItem,
                firedAtMillis = System.currentTimeMillis()
            )
        )
        notificationsRepository.increaseUnreadNotificationsCounter()
    }

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }

}