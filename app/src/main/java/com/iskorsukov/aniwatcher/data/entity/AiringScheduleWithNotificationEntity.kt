package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AiringScheduleWithNotificationEntity(
    @Embedded
    val airingScheduleEntity: AiringScheduleEntity,
    @Relation(
        parentColumn = "airingScheduleItemId",
        entityColumn = "airingScheduleItemRelationId"
    )
    val notificationItemEntity: NotificationItemEntity
)
