package com.iskorsukov.aniwatcher.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("DELETE FROM following WHERE mediaItemRelationId IN (:mediaIdList)")
    suspend fun unfollowMedia(mediaIdList: List<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Query("DELETE FROM media")
    suspend fun clearMedia()

    @Query("DELETE FROM airing WHERE airingAt < strftime('%s', 'now')")
    suspend fun clearAiredSchedules()
}