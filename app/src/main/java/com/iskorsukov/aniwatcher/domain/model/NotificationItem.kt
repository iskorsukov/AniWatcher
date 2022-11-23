package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity

data class NotificationItem(
    val id: Int? = null,
    val firedAtMillis: Long = System.currentTimeMillis(),
    val airingScheduleItem: AiringScheduleItem
) {
    companion object {
        fun fromEntity(mediaItemEntity: MediaItemEntity, airingScheduleAndNotificationEntity: AiringScheduleAndNotificationEntity): NotificationItem {
            val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)
            return with(airingScheduleAndNotificationEntity) {
                NotificationItem(
                    notificationItemEntity.notificationItemId!!,
                    notificationItemEntity.firedAtMillis,
                    AiringScheduleItem.fromEntity(airingScheduleEntity, mediaItem)
                )
            }
        }
    }
}
