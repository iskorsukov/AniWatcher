package com.iskorsukov.aniwatcher.data.executor

import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesEntity
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaDatabaseExecutor(
    private val mediaDatabase: MediaDatabase
) {
    private val mediaDao = mediaDatabase.mediaDao()

    suspend fun updateMedia(mediaEntityList: List<MediaItemWithAiringSchedulesEntity>) {
        withContext(Dispatchers.IO) {
            mediaDatabase.runInTransaction {
                mediaDao.clearMedia()
                mediaDao.insertMedia(mediaEntityList.map { it.mediaItemEntity })
                mediaDao.insertSchedules(mediaEntityList.map { it.airingScheduleEntityList }.flatten())
            }
        }
    }
}