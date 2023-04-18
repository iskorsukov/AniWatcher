package com.iskorsukov.aniwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun mediaDao(): MediaDao
}