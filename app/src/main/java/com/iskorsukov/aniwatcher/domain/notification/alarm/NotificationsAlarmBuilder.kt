package com.iskorsukov.aniwatcher.domain.notification.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object NotificationsAlarmBuilder {

    private const val NOTIFICATIONS_ALARM_REQUEST_CODE = 1117

    /**
     * Build a [PendingIntent] for the notifications check alarm
     *
     * @param context context
     * @return [PendingIntent] for the notifications check alarm
     */
    fun buildAlarmIntent(context: Context): PendingIntent {
        val receiverIntent = Intent(context, NotificationsAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            NOTIFICATIONS_ALARM_REQUEST_CODE,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Build a [PendingIntent] for cancelling existing notifications check alarm
     *
     * @param context context
     * @return [PendingIntent] for existing notifications check alarm
     */
    fun buildCancellationCheckAlarmIntent(context: Context): PendingIntent? {
        val receiverIntent = Intent(context, NotificationsAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            NOTIFICATIONS_ALARM_REQUEST_CODE,
            receiverIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
    }
}