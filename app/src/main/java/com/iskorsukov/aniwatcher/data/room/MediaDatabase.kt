package com.iskorsukov.aniwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.NotificationItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class,
        FollowingEntity::class,
        NotificationItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun mediaDao(): MediaDao

    abstract fun notificationsDao(): NotificationsDao
}