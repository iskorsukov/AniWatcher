package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.AiringScheduleWithNotificationEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity

data class NotificationItem(
    val id: Int,
    val firedAtMillis: Long,
    val mediaItem: MediaItem,
    val airingScheduleItem: AiringScheduleItem
) {
    companion object {
        fun fromEntity(mediaItemEntity: MediaItemEntity, airingScheduleWithNotificationEntity: AiringScheduleWithNotificationEntity): NotificationItem {
            val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)
            return with(airingScheduleWithNotificationEntity) {
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
