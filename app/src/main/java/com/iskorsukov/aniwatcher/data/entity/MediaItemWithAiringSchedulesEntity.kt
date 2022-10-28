package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MediaItemWithAiringSchedulesEntity(
    @Embedded val mediaItemEntity: MediaItemEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "mediaItemRelationId"
    )
    val airingScheduleEntityList: List<AiringScheduleEntity>
)