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
            childColumns = ["mediaItemRelationId"]
        )
    ]
)
data class AiringScheduleEntity(
    @PrimaryKey val airingScheduleItemId: Int,
    val airingAt: Int,
    val episode: Int,
    val mediaItemRelationId: Int
) {
    companion object {
        fun fromData(data: SeasonAiringDataQuery.AiringScheduleNode): AiringScheduleEntity {
            return data.run {
                AiringScheduleEntity(
                    airingScheduleItemId = id,
                    airingAt = airingAt,
                    episode = episode,
                    mediaItemRelationId = mediaId
                )
            }
        }
    }
}