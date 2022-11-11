package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesAndFollowingEntity
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaDatabaseExecutor @Inject constructor(
    private val mediaDatabase: MediaDatabase
) {
    private val mediaDao = mediaDatabase.mediaDao()

    val mediaDataFlow: Flow<List<MediaItemWithAiringSchedulesAndFollowingEntity>> = mediaDao.getAll()

    suspend fun updateMedia(mediaEntityList: List<MediaItemWithAiringSchedulesEntity>) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                mediaDao.clearMedia()
                mediaDao.insertMedia(mediaEntityList.map { it.mediaItemEntity })
                mediaDao.insertSchedules(mediaEntityList.map { it.airingScheduleEntityList }.flatten())
            }
        }
    }

    fun getMediaWithAiringSchedulesAndFollowing(mediaItemId: Int): Flow<MediaItemWithAiringSchedulesAndFollowingEntity> {
        return mediaDao.getById(mediaItemId)
    }

    suspend fun followMedia(mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            mediaDao.followMedia(FollowingEntity(null, mediaItemId))
        }
    }

    suspend fun unfollowMedia(mediaItemId: Int) {
        withContext(DispatcherProvider.io()) {
            mediaDao.unfollowMedia(mediaItemId)
        }
    }

    suspend fun clearAiredSchedules() {
        withContext(DispatcherProvider.io()) {
            mediaDao.clearAiredSchedules()
        }
    }
}