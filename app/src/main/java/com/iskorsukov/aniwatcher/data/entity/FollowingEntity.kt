package com.iskorsukov.aniwatcher.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "following",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaItemRelationId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class FollowingEntity(
    @PrimaryKey(autoGenerate = true) val followingEntryId: Int?,
    @ColumnInfo(index = true) val mediaItemRelationId: Int
)