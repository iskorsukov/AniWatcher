package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.*
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Transaction
    @Query("SELECT * FROM media LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId")
    fun getAll(): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>>

    @Transaction
    @Query("SELECT * FROM media LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE mediaId = :mediaItemId")
    fun getById(mediaItemId: Int): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Transaction
    @Query("DELETE FROM media")
    suspend fun clearMedia()

    @Transaction
    @Query("DELETE FROM airing")
    suspend fun clearAiringSchedules()
}