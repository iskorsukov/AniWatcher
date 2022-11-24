package com.iskorsukov.aniwatcher.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.model.NotificationItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.service.util.NotificationBuilderHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AiringNotificationInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val airingRepository: AiringRepository,
    private val notificationsRepository: NotificationsRepository,
    private val notificationManagerCompat: NotificationManagerCompat,
    settingsRepository: SettingsRepository,
): AiringNotificationInteractor {

    private val coroutineScope = CoroutineScope(DispatcherProvider.default() + Job())

    // NOTE: may be smart to synchronize access to runningJob to avoid races
    private var runningJob: Job? = null

    private val notificationsFlow = notificationsRepository.notificationsFlow

    private val followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        map.filter { it.key.isFollowing }
    }.combine(notificationsFlow) { followingMap, notificationsList ->
        followingMap.mapValues { followingEntry ->
            followingEntry.value.filterNot {
                    item -> notificationsList.any { it.airingScheduleItem.id == item.id }
            }
        }.filter { it.value.isNotEmpty() }
    }.distinctUntilChanged()

    private val settingsStateFlow = settingsRepository.settingsStateFlow

    override fun startNotificationChecking() {
        if (runningJob?.isActive == true || !settingsStateFlow.value.notificationsEnabled) return
        createNotificationChannel()
        runningJob = coroutineScope.launch {
            airingRepository.clearAiredSchedules()
            while (true) {
                if (!settingsStateFlow.value.notificationsEnabled) {
                    stopNotificationChecking()
                    awaitCancellation()
                }
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
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    private suspend fun fireNotificationsIfNeeded(mediaWithAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>) {
        val timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        mediaWithAiringSchedulesMap.values.flatten().forEach { airingScheduleItem ->
            if (airingScheduleItem.airingAt - timeInSeconds <= 0) {
                fireAiredNotification(airingScheduleItem)
            }
        }
    }

    private suspend fun fireAiredNotification(airingScheduleItem: AiringScheduleItem) {
        val notification = NotificationBuilderHelper.buildNotification(context, airingScheduleItem)
        notificationManagerCompat.notify(airingScheduleItem.id, notification)
        notificationsRepository.saveNotification(
            NotificationItem(
                airingScheduleItem = airingScheduleItem
            )
        )
    }

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }
}