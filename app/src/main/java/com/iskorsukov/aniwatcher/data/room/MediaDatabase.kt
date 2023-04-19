package com.iskorsukov.aniwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun mediaDao(): MediaDao
}