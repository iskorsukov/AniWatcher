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
    @Query("SELECT * FROM media")
    fun getAll(): Flow<List<MediaItemWithAiringSchedulesAndFollowingEntity>>

    @Transaction
    @Query("SELECT * FROM media WHERE mediaId = :mediaItemId")
    fun getById(mediaItemId: Int): Flow<MediaItemWithAiringSchedulesAndFollowingEntity>

    @Insert
    suspend fun followMedia(followingEntity: FollowingEntity)

    @Query("DELETE FROM following where mediaItemRelationId = :mediaId")
    suspend fun unfollowMedia(mediaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Query("DELETE FROM media")
    suspend fun clearMedia()

    @Query("DELETE FROM airing WHERE airingAt < strftime('%s', 'now')")
    suspend fun clearAiredSchedules()
}