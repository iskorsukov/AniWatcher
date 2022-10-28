package com.iskorsukov.aniwatcher.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery

@Entity(
    tableName = "airing",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaItemRelationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AiringScheduleEntity(
    @PrimaryKey val id: Int,
    val airingAt: Int,
    val episode: Int,
    val mediaItemRelationId: Int
) {
    companion object {
        fun fromData(data: SeasonAiringDataQuery.Node): AiringScheduleEntity {
            return data.run {
                AiringScheduleEntity(
                    id = id,
                    airingAt = airingAt,
                    episode = episode,
                    mediaItemRelationId = mediaId
                )
            }
        }
    }
}