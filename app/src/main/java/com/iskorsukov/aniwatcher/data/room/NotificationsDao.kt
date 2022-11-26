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
    fun getAll(): Flow<Map<MediaItemEntity, List<AiringScheduleAndNotificationEntity>>>

    @Transaction
    @Query(
        "SELECT * FROM media " +
                "LEFT JOIN airing ON mediaId = airing.mediaItemRelationId " +
                "WHERE mediaId IN (SELECT following.mediaItemRelationId FROM following) " +
                "AND airingAt < strftime('%s', 'now') " +
                "AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications) "
    )
    fun getPending(): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)
}