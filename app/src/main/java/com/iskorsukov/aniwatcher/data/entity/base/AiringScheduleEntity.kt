package com.iskorsukov.aniwatcher.data.entity.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
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
    @ColumnInfo(index = true) val mediaItemRelationId: Int
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

        fun fromData(data: RangeAiringDataQuery.AiringScheduleNode): AiringScheduleEntity {
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