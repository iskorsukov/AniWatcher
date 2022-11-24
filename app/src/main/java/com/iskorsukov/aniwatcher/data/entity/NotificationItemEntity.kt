package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = AiringScheduleEntity::class,
            parentColumns = ["airingScheduleItemId"],
            childColumns = ["airingScheduleItemRelationId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val notificationItemId: Int?,
    val firedAtMillis: Long,
    val airingScheduleItemRelationId: Int
)