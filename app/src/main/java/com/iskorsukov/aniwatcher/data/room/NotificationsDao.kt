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
                "AND airingAt < :currentTimeInSeconds " +
                "AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications) "
    )
    suspend fun getPending(currentTimeInSeconds: Int): Map<MediaItemEntity, List<AiringScheduleEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)

    @Transaction
    @Query("DELETE FROM notifications WHERE airingScheduleItemRelationId IN (SELECT airingScheduleItemId FROM airing WHERE airing.mediaItemRelationId = :mediaId)")
    suspend fun clearNotificationsByMediaId(mediaId: Int)

    @Transaction
    @Query("DELETE FROM notifications WHERE airingScheduleItemRelationId IN (SELECT airingScheduleItemId FROM airing WHERE airing.mediaItemRelationId IN (:mediaIdList))")
    suspend fun clearNotificationsByMediaId(mediaIdList: List<Int>)
}