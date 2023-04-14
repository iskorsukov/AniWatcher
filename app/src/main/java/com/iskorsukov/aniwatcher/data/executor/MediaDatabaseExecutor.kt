package com.iskorsukov.aniwatcher.data.executor

import androidx.room.withTransaction
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaDatabaseExecutor @Inject constructor(
    private val mediaDatabase: MediaDatabase
) {
    private val mediaDao = mediaDatabase.mediaDao()

    val mediaDataFlow: Flow<Map<MediaItemEntity, List<AiringScheduleEntity>>> =
        mediaDao.getAll()

    suspend fun updateMedia(mediaToAiringSchedulesMap: Map<MediaItemEntity, List<AiringScheduleEntity>>) {
        withContext(DispatcherProvider.io()) {
            mediaDatabase.withTransaction {
                mediaDao.clearAiringSchedules()
                mediaDao.clearMedia()
                mediaDao.insertMedia(mediaToAiringSchedulesMap.keys.toList())
                mediaDao.insertSchedules(mediaToAiringSchedulesMap.values.flatten())
            }
        }
    }

    fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItemEntity, List<AiringScheduleEntity>>?> {
        return mediaDao.getById(mediaItemId).map { mediaToSchedulesMap ->
            mediaToSchedulesMap.entries.firstOrNull()?.toPair()
        }
    }
}