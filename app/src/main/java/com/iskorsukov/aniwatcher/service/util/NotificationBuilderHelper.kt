package com.iskorsukov.aniwatcher.service.util

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.service.AiringNotificationInteractorImpl

object NotificationBuilderHelper {

    fun buildNotification(context: Context, airingScheduleItem: AiringScheduleItem): Notification {
        return NotificationCompat.Builder(
            context,
            AiringNotificationInteractorImpl.CHANNEL_ID
        )
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
            .build()
    }
}