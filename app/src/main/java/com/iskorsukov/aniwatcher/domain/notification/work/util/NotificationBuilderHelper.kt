package com.iskorsukov.aniwatcher.domain.notification.work.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.notification.work.NotificationsWorker

object NotificationBuilderHelper {

    private const val GROUP_ID = "airing"

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
                    airingScheduleItem.getAiringAtDateTimeFormatted()
                )
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_ID)
            .build()
    }

    fun buildGroupSummaryNotification(
        context: Context,
        airingScheduleItemList: List<AiringScheduleItem>
    ): Notification? {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as? NotificationManager
        return if (notificationManager != null) {
            val existingMessages = getActiveNotificationsMessageMap(notificationManager)
            val newMessages = getNotificationsMessageMap(context, airingScheduleItemList)
            val messages = existingMessages.plus(newMessages)
            if (messages.isEmpty()) {
                return null
            }
            val style = NotificationCompat.InboxStyle()
            messages.values.forEach { style.addLine(it) }
            return NotificationCompat.Builder(
                context,
                NotificationsWorker.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.aniwatcher_icon_fg_white)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setGroup(GROUP_ID)
                .build()
        } else {
            null
        }
    }

    private fun getActiveNotificationsMessageMap(notificationManager: NotificationManager): Map<Int, String> {
        return notificationManager.activeNotifications.associate { statusBarNotification ->
                statusBarNotification.notification.let {
                    val id = statusBarNotification.id
                    val title = it.extras.getString("android.title")
                    val text = it.extras.getString("android.text")
                    if (title == null) {
                        id to ""
                    } else {
                        id to "$title $text"
                    }
                }
            }.filterValues { it.isNotBlank() }
    }

    private fun getNotificationsMessageMap(context: Context, airingScheduleItemList: List<AiringScheduleItem>): Map<Int, String> {
        return airingScheduleItemList.associate { airingScheduleItem ->
            val id = airingScheduleItem.id
            val title = airingScheduleItem.mediaItem.title.baseText()
            val text = String.format(
                context.getString(R.string.episode_aired_at_time_text),
                airingScheduleItem.episode,
                airingScheduleItem.getAiringAtDateTimeFormatted()
            )
            if (title.isBlank()) {
                id to ""
            } else {
                id to "$title $text"
            }
        }.filterValues { it.isNotBlank() }
    }
}