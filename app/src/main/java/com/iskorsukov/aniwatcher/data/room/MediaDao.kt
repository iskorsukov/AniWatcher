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
    fun getAll(): Flow<MediaItemWithAiringSchedulesAndFollowingEntity>

    @Insert
    fun followMedia(followingEntity: FollowingEntity)

    @Query("DELETE FROM following where mediaItemRelationId = :mediaId")
    fun unfollowMedia(mediaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Query("DELETE FROM media")
    fun clearMedia()
}