package com.iskorsukov.aniwatcher.data.entity.combined

import androidx.room.Embedded
import androidx.room.Relation
import com.iskorsukov.aniwatcher.data.entity.base.FollowingEntity
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity

data class MediaItemAndFollowingEntity(
    @Embedded val mediaItemEntity: MediaItemEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "mediaItemRelationId"
    )
    val followingEntity: FollowingEntity?
)