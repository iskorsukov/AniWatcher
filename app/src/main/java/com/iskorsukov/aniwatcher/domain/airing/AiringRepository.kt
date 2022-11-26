package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface AiringRepository {

    val mediaWithSchedulesFlow: Flow<Map<MediaItem, List<AiringScheduleItem>>>

    fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>?>

    suspend fun loadSeasonAiringData(year: Int, season: String)

    suspend fun followMedia(mediaItem: MediaItem)

    suspend fun unfollowMedia(mediaItem: MediaItem)

    suspend fun unfollowMedia(mediaItemList: List<MediaItem>)
}