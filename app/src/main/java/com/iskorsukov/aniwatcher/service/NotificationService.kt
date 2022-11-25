package com.iskorsukov.aniwatcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService: Service() {

    @Inject
    lateinit var airingNotificationInteractorImpl: AiringNotificationInteractor

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        airingNotificationInteractorImpl.startNotificationChecking()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        airingNotificationInteractorImpl.stopNotificationChecking()
    }

}