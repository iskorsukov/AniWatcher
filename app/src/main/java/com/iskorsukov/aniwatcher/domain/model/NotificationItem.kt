package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity

data class NotificationItem(
    val id: Int? = null,
    val firedAtMillis: Long = System.currentTimeMillis(),
    val airingScheduleItem: AiringScheduleItem,
    val mediaItem: MediaItem
) {
    companion object {
        fun fromEntity(
            mediaItemEntity: MediaItemEntity,
            airingScheduleAndNotificationEntity: AiringScheduleAndNotificationEntity
        ): NotificationItem {
            return with(airingScheduleAndNotificationEntity) {
                NotificationItem(
                    notificationItemEntity.notificationItemId!!,
                    notificationItemEntity.firedAtMillis,
                    AiringScheduleItem.fromEntity(airingScheduleEntity),
                    MediaItem.fromEntity(mediaItemEntity, true)
                )
            }
        }
    }
}
