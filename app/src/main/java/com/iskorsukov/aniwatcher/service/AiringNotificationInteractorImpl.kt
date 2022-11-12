package com.iskorsukov.aniwatcher.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AiringNotificationInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val airingRepository: AiringRepository
): AiringNotificationInteractor {

    private val coroutineScope = CoroutineScope(DispatcherProvider.default() + Job())

    // NOTE: may be smart to synchronize access to runningJob to avoid races
    private var runningJob: Job? = null

    private val followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        map.filter { it.key.isFollowing }.filter { it.value.isNotEmpty() }
    }.distinctUntilChanged()

    override fun startNotificationChecking() {
        if (runningJob?.isActive == true) return
        createNotificationChannel()
        runningJob = coroutineScope.launch {
            airingRepository.clearAiredSchedules()
            while (true) {
                followingMediaFlow.firstOrNull()?.let {
                    fireNotificationsIfNeeded(it)
                }
                airingRepository.clearAiredSchedules()
                delay(TimeUnit.MINUTES.toMillis(5L))
            }
        }
    }

    override fun stopNotificationChecking() {
        runningJob?.cancel()
        runningJob = null
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun fireNotificationsIfNeeded(mediaWithAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>) {
        val timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        mediaWithAiringSchedulesMap.values.flatten().forEach { airingScheduleItem ->
            if (airingScheduleItem.airingAt - timeInSeconds <= 0) {
                fireAiredNotification(airingScheduleItem)
            }
        }
    }

    private fun fireAiredNotification(airingScheduleItem: AiringScheduleItem) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(airingScheduleItem.mediaItem.title.baseText())
            .setContentText(
                String.format(
                    context.getString(R.string.episode_aired_at_time_text),
                    airingScheduleItem.episode,
                    airingScheduleItem.getAiringAtTimeFormatted()
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(airingScheduleItem.id, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }
}