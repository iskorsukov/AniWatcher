package com.iskorsukov.aniwatcher.domain.notification.work.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.notification.NotificationsInteractor

object NotificationBuilderHelper {

    private const val GROUP_ID = "airing"

    /**
     * Builds a notification for aired episode
     *
     * @param context context
     * @param airingScheduleItem aired airing schedule (episode)
     * @return notification
     */
    fun buildNotification(context: Context, airingScheduleItem: AiringScheduleItem, mediaItem: MediaItem): Notification {
        return NotificationCompat.Builder(
            context,
            NotificationsInteractor.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.aniwatcher_icon_fg_white)
            .setContentTitle(mediaItem.title.baseText())
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
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .build()
    }

    /**
     * Builds a group notification with summary
     *
     * @param context context
     * @param airingSchedulePairList pairs of aired schedules and related media items
     * @return notification or null if notification manager not available
     */
    fun buildGroupSummaryNotification(
        context: Context,
        airingSchedulePairList: List<Pair<AiringScheduleItem, MediaItem>>
    ): Notification? {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as? NotificationManager
        return if (notificationManager != null) {
            val existingMessages = getActiveNotificationsMessageMap(notificationManager)
            val newMessages = getNotificationsMessageMap(context, airingSchedulePairList)
            val messages = existingMessages.plus(newMessages)
            if (messages.isEmpty()) {
                return null
            }
            val style = NotificationCompat.InboxStyle()
            messages.values.forEach { style.addLine(it) }
            return NotificationCompat.Builder(
                context,
                NotificationsInteractor.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.aniwatcher_icon_fg_white)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setGroup(GROUP_ID)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .build()
        } else {
            null
        }
    }

    /**
     * Get currently displayed notification messages
     *
     * @param notificationManager notification manager
     * @return map of notification id to message
     */
    private fun getActiveNotificationsMessageMap(notificationManager: NotificationManager): Map<Int, String> {
        return notificationManager.activeNotifications
            .associate { statusBarNotification ->
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
            }
            .filterValues { it.isNotBlank() }
    }

    /**
     * Get summary messages for group notification summary
     *
     * @param context context
     * @param airingSchedulePairList pairs of airing schedules and related media items
     * @return map of notification id to message
     */
    private fun getNotificationsMessageMap(
        context: Context,
        airingSchedulePairList: List<Pair<AiringScheduleItem, MediaItem>>
    ): Map<Int, String> {
        return airingSchedulePairList.associate { airingSchedulePair ->
            val id = airingSchedulePair.first.id
            val title = airingSchedulePair.second.title.baseText()
            val text = String.format(
                context.getString(R.string.episode_aired_at_time_text),
                airingSchedulePair.first.episode,
                airingSchedulePair.first.getAiringAtDateTimeFormatted()
            )
            if (title.isBlank()) {
                id to ""
            } else {
                id to "$title $text"
            }
        }.filterValues { it.isNotBlank() }
    }
}