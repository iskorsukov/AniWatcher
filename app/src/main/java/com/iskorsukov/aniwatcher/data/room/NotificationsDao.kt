package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.base.NotificationItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.AiringScheduleAndNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationsDao {

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query(
        "SELECT * FROM media INNER JOIN " +
                "(SELECT * FROM airing INNER JOIN notifications ON airing.airingScheduleItemId = notifications.airingScheduleItemRelationId) " +
                "ON media.mediaId = mediaItemRelationId"
    )
    fun getAll(): Flow<Map<MediaItemEntity, List<AiringScheduleAndNotificationEntity>>>

    @Transaction
    @Query(
        "SELECT * FROM media INNER JOIN airing ON mediaId = airing.mediaItemRelationId " +
                "WHERE airingAt < :currentTimeInSeconds " +
                "AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications) "
    )
    suspend fun getPending(currentTimeInSeconds: Int): Map<MediaItemEntity, List<AiringScheduleEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)

    @Transaction
    @Query("DELETE FROM notifications WHERE airingScheduleItemRelationId IN (SELECT airingScheduleItemId FROM airing WHERE airing.mediaItemRelationId IN (:mediaId))")
    suspend fun clearNotificationsByMediaId(vararg mediaId: Int)
}