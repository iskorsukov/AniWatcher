package com.iskorsukov.aniwatcher.domain.notification

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem

interface NotificationsInteractor {

    fun createNotificationsChannel()

    fun fireAiredNotifications(airingScheduleItemList: List<AiringScheduleItem>)

    fun clearStatusBarNotifications()

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }
}