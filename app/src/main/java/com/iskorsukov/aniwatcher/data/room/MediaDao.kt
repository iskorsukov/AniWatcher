package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.entity.combined.MediaItemAndFollowingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN (SELECT * FROM airing WHERE airingAt > :currentTimeInSeconds) AS airing ON media.mediaId = airing.mediaItemRelationId")
    fun getAllNotAired(currentTimeInSeconds: Int): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Transaction
    @Query("SELECT * FROM media " +
            "LEFT JOIN following ON media.mediaId = following.mediaItemRelationId " +
            "LEFT JOIN (SELECT * FROM airing WHERE airingAt > :currentTimeInSeconds) AS airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE mediaId = :mediaItemId")
    fun getByIdNotAired(mediaItemId: Int, currentTimeInSeconds: Int): Flow<Map<MediaItemAndFollowingEntity, List<AiringScheduleEntity>>>

    @Insert
    suspend fun followMedia(followingEntity: FollowingEntity)

    @Query("DELETE FROM following WHERE mediaItemRelationId IN (:mediaId)")
    suspend fun unfollowMedia(vararg mediaId: Int)

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
    @Query("DELETE FROM airing WHERE airing.mediaItemRelationId = :mediaId AND airingAt < :currentTimeInSeconds AND airingScheduleItemId NOT IN (SELECT airingScheduleItemRelationId FROM notifications)")
    suspend fun clearNotNotifiedAiredSchedules(mediaId: Int, currentTimeInSeconds: Int)
}