package com.iskorsukov.aniwatcher.domain.notification.work.util

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.notification.work.NotificationsWorker

object NotificationBuilderHelper {

    fun buildNotification(context: Context, airingScheduleItem: AiringScheduleItem): Notification {
        return NotificationCompat.Builder(
            context,
            NotificationsWorker.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.aniwatcher_icon_fg_white)
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