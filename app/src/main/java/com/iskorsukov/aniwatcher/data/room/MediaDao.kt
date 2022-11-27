package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE airingAt > strftime('%s', 'now')")
    fun getAllNotAired(): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE mediaId = :mediaItemId AND airingAt > strftime('%s', 'now')")
    fun getByIdNotAired(mediaItemId: Int): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Insert
    suspend fun followMedia(followingEntity: FollowingEntity)

    @Query("DELETE FROM following WHERE mediaItemRelationId = :mediaId")
    suspend fun unfollowMedia(mediaId: Int)

    @Query("DELETE FROM following WHERE mediaItemRelationId IN (:mediaIdList)")
    suspend fun unfollowMedia(mediaIdList: List<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Transaction
    @Query("DELETE FROM media WHERE mediaId NOT IN (SELECT mediaItemRelationId FROM following)")
    suspend fun clearNotFollowedMedia()

    @Transaction
    @Query("DELETE FROM airing WHERE airing.mediaItemRelationId NOT IN (SELECT mediaItemRelationId FROM following) AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications)")
    suspend fun clearNotFollowedAiringSchedules()

    @Transaction
    @Query("DELETE FROM airing WHERE airing.mediaItemRelationId = :mediaId AND airingAt < strftime('%s', 'now') AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications)")
    suspend fun clearNotNotifiedAiredSchedules(mediaId: Int)
}