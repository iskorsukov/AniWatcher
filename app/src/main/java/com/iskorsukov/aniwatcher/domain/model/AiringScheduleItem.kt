package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.WeekAiringDataQuery

data class AiringScheduleItem(
    val id: Int,
    val airingAt: Int,
    val episode: Int,
    val mediaItem: MediaItem
) {
    companion object {
        fun fromData(data: WeekAiringDataQuery.AiringSchedule): AiringScheduleItem {
            return data.run {
                AiringScheduleItem(
                    id = id,
                    airingAt = airingAt,
                    episode = episode,
                    mediaItem = media!!.let { MediaItem.fromData(it) }
                )
            }
        }
    }
}
