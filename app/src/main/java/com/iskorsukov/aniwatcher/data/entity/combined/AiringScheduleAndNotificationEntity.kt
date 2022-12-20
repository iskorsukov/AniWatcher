package com.iskorsukov.aniwatcher.data.entity.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity

data class AiringScheduleAndNotificationEntity(
    @Embedded
    val airingScheduleEntity: AiringScheduleEntity,
    @Relation(
        parentColumn = "airingScheduleItemId",
        entityColumn = "airingScheduleItemRelationId"
    )
    val notificationItemEntity: NotificationItemEntity
)
