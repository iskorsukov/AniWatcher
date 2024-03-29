package com.iskorsukov.aniwatcher.domain.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.notification.work.util.NotificationBuilderHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManagerCompat: NotificationManagerCompat
) : NotificationsInteractor {

    override fun createNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(NotificationsInteractor.CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    override fun fireAiredNotifications(airingSchedulePairList: List<Pair<AiringScheduleItem, MediaItem>>) {
        if (airingSchedulePairList.isEmpty()) return
        airingSchedulePairList.forEach { airingScheduleItem ->
            val notification = NotificationBuilderHelper.buildNotification(
                context, airingScheduleItem.first, airingScheduleItem.second
            )
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManagerCompat.notify(airingScheduleItem.first.id, notification)
            }
        }
        fireUpdatedGroupNotification(airingSchedulePairList)
    }

    private fun fireUpdatedGroupNotification(airingSchedulePairList: List<Pair<AiringScheduleItem, MediaItem>>) {
        val notification = NotificationBuilderHelper.buildGroupSummaryNotification(
            context,
            airingSchedulePairList
        )
        if (notification != null) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManagerCompat.notify(GROUP_NOTIFICATION_ID, notification)
            }
        } else {
            notificationManagerCompat.cancel(GROUP_NOTIFICATION_ID)
        }
    }

    override fun clearStatusBarNotifications() {
        notificationManagerCompat.cancelAll()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
            ?.cancelAll()
    }

    companion object {
        private const val GROUP_NOTIFICATION_ID = 554
    }
}