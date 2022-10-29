package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaDatabaseExecutor(
    private val mediaDatabase: MediaDatabase
) {
    private val mediaDao = mediaDatabase.mediaDao()

    suspend fun updateMedia(mediaEntityList: List<MediaItemWithAiringSchedulesEntity>) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                mediaDao.clearMedia()
                mediaDao.insertMedia(mediaEntityList.map { it.mediaItemEntity })
                mediaDao.insertSchedules(mediaEntityList.map { it.airingScheduleEntityList }.flatten())
            }
        }
    }
}