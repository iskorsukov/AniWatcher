package com.iskorsukov.aniwatcher.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity

@Database(
    entities = [
        MediaItemEntity::class,
        AiringScheduleEntity::class,
        NotificationItemEntity::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 2,
            to = 3,
            spec = PersistentMediaDatabase.RemoveNextEpisodeAiringAtMigration::class
        )
    ]
)
abstract class PersistentMediaDatabase: RoomDatabase() {

    abstract fun persistentMediaDao(): PersistentMediaDao

    abstract fun notificationsDao(): NotificationsDao

    @DeleteColumn("media", "nextEpisodeAiringAt")
    class RemoveNextEpisodeAiringAtMigration: AutoMigrationSpec

    companion object {
        val MIGRATION_1_2 = Migration(1, 2) { db ->
            db.execSQL("ALTER TABLE media ADD COLUMN status varchar(255)")
        }
    }
}