package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesAndFollowingEntity

data class AiringScheduleItem(
    val id: Int,
    val airingAt: Int,
    val episode: Int,
    val mediaItem: MediaItem
) {
    companion object {
        fun fromEntity(entity: MediaItemWithAiringSchedulesAndFollowingEntity): List<AiringScheduleItem> {
            val mediaItem = MediaItem.fromEntity(
                entity.mediaItemWithAiringSchedulesEntity.mediaItemEntity,
                entity.followingEntity
            )
            return entity.mediaItemWithAiringSchedulesEntity.run {
                airingScheduleEntityList.map {
                    AiringScheduleItem(
                        id = it.id,
                        airingAt = it.airingAt,
                        episode = it.episode,
                        mediaItem = mediaItem
                    )
                }
            }
        }
    }
}
