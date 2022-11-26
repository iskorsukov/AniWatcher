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
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.notification.work.util.LocalClockSystem
import com.iskorsukov.aniwatcher.domain.notification.work.util.NotificationBuilderHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    airingRepository: AiringRepository,
    private val clock: LocalClockSystem,
    private val notificationsRepository: NotificationsRepository,
    private val notificationManagerCompat: NotificationManagerCompat
): CoroutineWorker(appContext, workerParams) {

    private val followingMediaFlow = airingRepository.mediaWithSchedulesFlow
        .map { map ->
            map.filter { it.key.isFollowing }.filter { it.value.isNotEmpty() }
        }
        .distinctUntilChanged()

    override suspend fun doWork(): Result {
        createNotificationChannel()
        followingMediaFlow.firstOrNull()?.let {
            fireNotificationsIfNeeded(it)
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

    private suspend fun fireNotificationsIfNeeded(mediaWithAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>) {
        val timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(clock.currentTimeMillis())
        mediaWithAiringSchedulesMap.values.flatten().forEach { airingScheduleItem ->
            // if episode aired during last delay
            if (timeInSeconds - airingScheduleItem.airingAt in 0 until DELAY_SECONDS) {
                fireAiredNotification(airingScheduleItem)
            }
        }
    }

    private suspend fun fireAiredNotification(airingScheduleItem: AiringScheduleItem) {
        val notification = NotificationBuilderHelper.buildNotification(applicationContext, airingScheduleItem)
        notificationManagerCompat.notify(airingScheduleItem.id, notification)
        notificationsRepository.saveNotification(
            NotificationItem(
                airingScheduleItem = airingScheduleItem,
                firedAtMillis = clock.currentTimeMillis()
            )
        )
        notificationsRepository.increaseUnreadNotificationsCounter()
    }

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
        const val DELAY_SECONDS = 15L * 60
    }

}