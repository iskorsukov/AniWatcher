package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.AiringScheduleAndNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity

data class NotificationItem(
    val id: Int,
    val firedAtMillis: Long,
    val mediaItem: MediaItem,
    val airingScheduleItem: AiringScheduleItem
) {
    companion object {
        fun fromEntity(mediaItemEntity: MediaItemEntity, airingScheduleAndNotificationEntity: AiringScheduleAndNotificationEntity): NotificationItem {
            val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)
            return with(airingScheduleAndNotificationEntity) {
                NotificationItem(
                    notificationItemEntity.notificationItemId!!,
                    notificationItemEntity.firedAtMillis,
                    mediaItem,
                    AiringScheduleItem.fromEntity(airingScheduleEntity, mediaItem)
                )
            }
        }
    }
}
