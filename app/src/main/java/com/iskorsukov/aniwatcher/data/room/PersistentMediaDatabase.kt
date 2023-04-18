package com.iskorsukov.aniwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class,
        NotificationItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PersistentMediaDatabase: RoomDatabase() {

    abstract fun persistentMediaDao(): PersistentMediaDao

    abstract fun notificationsDao(): NotificationsDao
}