package com.iskorsukov.aniwatcher.domain.notification.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.iskorsukov.aniwatcher.domain.notification.work.NotificationsWorker

class NotificationsAlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val notificationsWorkRequest = OneTimeWorkRequestBuilder<NotificationsWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                NOTIFICATIONS_WORK_TAG,
                ExistingWorkPolicy.KEEP,
                notificationsWorkRequest
            )
        }
    }

    companion object {
        const val NOTIFICATIONS_WORK_TAG = "notifications_work"
    }
}