package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "following"
)
data class FollowingEntity(
    @PrimaryKey(autoGenerate = true) val followingEntryId: Int?,
    val mediaItemRelationId: Int
)