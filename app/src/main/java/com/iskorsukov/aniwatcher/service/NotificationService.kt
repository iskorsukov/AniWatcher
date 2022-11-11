package com.iskorsukov.aniwatcher.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService: Service() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(DispatcherProvider.default() + job)

    @Inject
    lateinit var airingRepository: AiringRepository

    private lateinit var followingMediaFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>>

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
            map.filter { it.key.isFollowing }.filter { it.value.isNotEmpty() }
        }.distinctUntilChanged()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            clearAiredSchedules()
            while (true) {
                fireNotificationsIfNeeded()
                delay(TimeUnit.MINUTES.toMillis(5L))
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun clearAiredSchedules() {
        airingRepository.clearAiredSchedules()
    }

    private suspend fun fireNotificationsIfNeeded() {
        val timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        followingMediaFlow.collectLatest { map ->
            map.values.flatten().forEach { airingScheduleItem ->
                if (airingScheduleItem.airingAt - timeInSeconds <= 0) {
                    fireAiredNotification(airingScheduleItem)
                }
            }
        }
        clearAiredSchedules()
    }

    private fun fireAiredNotification(airingScheduleItem: AiringScheduleItem) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(airingScheduleItem.mediaItem.title.baseText())
            .setContentText(
                String.format(
                    getString(R.string.episode_aired_at_time_text),
                    airingScheduleItem.episode,
                    airingScheduleItem.getAiringAtTimeFormatted()
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(airingScheduleItem.id, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }
}