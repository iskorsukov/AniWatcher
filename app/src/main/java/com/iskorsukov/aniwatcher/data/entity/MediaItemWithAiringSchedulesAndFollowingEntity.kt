package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MediaItemWithAiringSchedulesAndFollowingEntity(
    @Embedded val mediaItemWithAiringSchedulesEntity: MediaItemWithAiringSchedulesEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "mediaItemRelationId"
    )
    val followingEntity: FollowingEntity?
)
