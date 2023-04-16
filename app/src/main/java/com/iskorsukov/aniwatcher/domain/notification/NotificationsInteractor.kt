package com.iskorsukov.aniwatcher.domain.notification

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem

interface NotificationsInteractor {

    fun createNotificationsChannel()

    fun fireAiredNotifications(airingSchedulePairList: List<Pair<AiringScheduleItem, MediaItem>>)

    fun clearStatusBarNotifications()

    companion object {
        const val CHANNEL_ID = "AniWatcherAiring"
    }
}