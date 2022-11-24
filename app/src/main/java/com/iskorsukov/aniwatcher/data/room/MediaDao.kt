package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId")
    fun getAll(): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE mediaId = :mediaItemId")
    fun getById(mediaItemId: Int): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Insert
    suspend fun followMedia(followingEntity: FollowingEntity)

    @Query("DELETE FROM following WHERE mediaItemRelationId = :mediaId")
    suspend fun unfollowMedia(mediaId: Int)

    @Transaction
    @Query("DELETE FROM following WHERE mediaItemRelationId IN (:mediaIdList)")
    suspend fun unfollowMedia(mediaIdList: List<Int>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Transaction
    @Query("DELETE FROM media WHERE mediaId NOT IN (SELECT mediaItemRelationId FROM following)")
    suspend fun clearMedia()

    @Transaction
    @Query("DELETE FROM airing WHERE airingAt < strftime('%s', 'now') AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications)")
    suspend fun clearAiredSchedules()
}