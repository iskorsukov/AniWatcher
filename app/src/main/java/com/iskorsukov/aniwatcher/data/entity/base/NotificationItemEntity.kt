package com.iskorsukov.aniwatcher.data.entity.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = AiringScheduleEntity::class,
            parentColumns = ["airingScheduleItemId"],
            childColumns = ["airingScheduleItemRelationId"]
        )
    ])
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true) val notificationItemId: Int?,
    val firedAtMillis: Long,
    @ColumnInfo(index = true) val airingScheduleItemRelationId: Int
)