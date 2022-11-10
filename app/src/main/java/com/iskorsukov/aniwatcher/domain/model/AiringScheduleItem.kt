package com.iskorsukov.aniwatcher.domain.model

import com.iskorsukov.aniwatcher.data.entity.MediaItemWithAiringSchedulesAndFollowingEntity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

data class AiringScheduleItem(
    val id: Int,
    val airingAt: Int,
    val episode: Int,
    val mediaItem: MediaItem
) {

    fun getAiringAtFormatted(): String {
        val formatter = SimpleDateFormat("MMMM dd',' HH':'mm", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { timeInMillis = (airingAt.toLong() * 1000) }
        return formatter.format(calendar.time)
    }

    fun getAiringInFormatted(timeInMinutes: Long): String? {
        val airingAtMinutes = airingAt.toLong() / 60
        val diffMinutes = airingAtMinutes - timeInMinutes
        if (diffMinutes < 0) return null

        val airingInDays = TimeUnit.MINUTES.toDays(diffMinutes)
        val airingInHours = TimeUnit.MINUTES.toHours(diffMinutes) - TimeUnit.DAYS.toHours(airingInDays)
        val airingInMinutes = diffMinutes - TimeUnit.HOURS.toMinutes(airingInHours) - TimeUnit.DAYS.toMinutes(airingInDays)
        return buildString {
            if (airingInDays > 0) {
                append("$airingInDays days, ")
            }
            if (airingInHours > 0) {
                append("$airingInHours hours, ")
            }
            if (airingInMinutes > 0) {
                append("$airingInMinutes minutes, ")
            }
            delete(length - 2, length)
        }
    }

    companion object {
        fun fromEntity(entity: MediaItemWithAiringSchedulesAndFollowingEntity): List<AiringScheduleItem> {
            val mediaItem = MediaItem.fromEntity(
                entity.mediaItemWithAiringSchedulesEntity.mediaItemEntity,
                entity.followingEntity
            )
            return entity.mediaItemWithAiringSchedulesEntity.run {
                airingScheduleEntityList.map {
                    AiringScheduleItem(
                        id = it.id,
                        airingAt = it.airingAt,
                        episode = it.episode,
                        mediaItem = mediaItem
                    )
                }
            }
        }
    }
}
