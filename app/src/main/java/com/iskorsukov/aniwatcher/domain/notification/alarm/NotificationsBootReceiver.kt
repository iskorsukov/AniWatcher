package com.iskorsukov.aniwatcher.domain.notification.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class NotificationsBootReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            // Schedule notifications check
            (context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
                ?.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    NotificationsAlarmBuilder.buildAlarmIntent(context)
                )
        }
    }
}