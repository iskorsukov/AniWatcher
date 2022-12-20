package com.iskorsukov.aniwatcher.domain.notification.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.iskorsukov.aniwatcher.domain.notification.work.NotificationsWorker

class NotificationsAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            // Enqueue notifications check work
            val notificationsWorkRequest = OneTimeWorkRequestBuilder<NotificationsWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                NOTIFICATIONS_WORK_TAG,
                ExistingWorkPolicy.KEEP,
                notificationsWorkRequest
            )
            // Reschedule alarm
            (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
                ?.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                    NotificationsAlarmBuilder.buildAlarmIntent(context)
                )
        }
    }

    companion object {
        const val NOTIFICATIONS_WORK_TAG = "notifications_work"
    }
}