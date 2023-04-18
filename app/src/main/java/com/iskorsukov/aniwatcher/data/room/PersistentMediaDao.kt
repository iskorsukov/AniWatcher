package com.iskorsukov.aniwatcher.data.room

import androidx.room.*
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersistentMediaDao {

    @Transaction
    @Query("SELECT * FROM media LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId")
    fun getAll(): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>>

    @Transaction
    @Query("SELECT * FROM media LEFT JOIN airing ON media.mediaId = airing.mediaItemRelationId " +
            "WHERE mediaId = :mediaItemId")
    fun getById(mediaItemId: Int): Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItemEntity: MediaItemEntity)

    @Transaction
    @Update
    suspend fun updateMedia(mediaItemEntityList: List<MediaItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(airingScheduleEntityList: List<AiringScheduleEntity>)

    @Query("DELETE FROM media WHERE mediaId = :mediaItemId")
    suspend fun deleteMedia(mediaItemId: Int)
}