package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MediaItemAndFollowingEntity(
    @Embedded val mediaItemEntity: MediaItemEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "mediaItemRelationId"
    )
    val followingEntity: FollowingEntity?
)