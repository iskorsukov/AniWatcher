package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {

    @Transaction
    @Query(
        "SELECT * FROM media " +
                "INNER JOIN " +
                "(SELECT * FROM airing INNER JOIN notifications " +
                "ON airing.airingScheduleItemId = notifications.airingScheduleItemRelationId) " +
                "ON media.mediaId = mediaItemRelationId"
    )
    fun getAll(): Flow<Map<MediaItemEntity, AiringScheduleWithNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)
}