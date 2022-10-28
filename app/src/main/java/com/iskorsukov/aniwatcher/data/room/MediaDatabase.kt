package com.iskorsukov.aniwatcher.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iskorsukov.aniwatcher.data.entity.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class,
        FollowingEntity::class
    ],
    version = 1
)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun mediaDao(): MediaDao
}