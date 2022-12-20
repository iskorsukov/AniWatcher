package com.iskorsukov.aniwatcher.domain.mapper

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import java.util.*

object MediaItemMapper {

    fun groupAiringSchedulesByDayOfWeek(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>
    ): Map<DayOfWeekLocal, List<AiringScheduleItem>> {
        val dayOfWeekSchedulesMap = mutableMapOf<DayOfWeekLocal, List<AiringScheduleItem>>()
        mediaItemToAiringSchedulesMap.forEach { entry ->
            val currentDayOfWeek = DayOfWeekLocal.ofCalendar(Calendar.getInstance())
            val currentDayEndInSeconds = DateTimeHelper.currentDayEndSeconds(Calendar.getInstance())

            // map media entry schedules to days of week
            val dayOfWeekSchedulesForEntryMap = mutableMapOf<DayOfWeekLocal, AiringScheduleItem>()
            entry.value.forEach inner@{ item ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = item.airingAt.toLong() * 1000
                }
                val dayOfWeek = DayOfWeekLocal.ofCalendar(calendar)
                // for current day of week show only schedules that air today, not a week or more away
                // this mostly helps visual clutter
                if (dayOfWeek == currentDayOfWeek && item.airingAt > currentDayEndInSeconds)
                    return@inner
                // save closes airing item for each day of week
                // usually there will be only one, but this allows to filter occasional stack
                // of multiple episodes airing on the same day
                if (item.airingAt < (dayOfWeekSchedulesForEntryMap[dayOfWeek]?.airingAt ?: Int.MAX_VALUE)) {
                    dayOfWeekSchedulesForEntryMap[dayOfWeek] = item
                }
            }

            // add grouped schedules of entry to global day of week map
            dayOfWeekSchedulesForEntryMap.forEach { (dayOfWeek, item) ->
                val globalDayOfWeekSchedules = dayOfWeekSchedulesMap[dayOfWeek]
                val updatedGlobalDayOfWeekSchedules = mutableListOf<AiringScheduleItem>().apply {
                    if (globalDayOfWeekSchedules != null) addAll(globalDayOfWeekSchedules)
                    add(item)
                }
                updatedGlobalDayOfWeekSchedules.sortBy { scheduleItem -> scheduleItem.airingAt }
                dayOfWeekSchedulesMap[dayOfWeek] = updatedGlobalDayOfWeekSchedules
            }
        }
        return dayOfWeekSchedulesMap
    }

    fun groupMediaWithNextAiringSchedule(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>
    ): Map<MediaItem, AiringScheduleItem?> {
        val mediaToNextAiringScheduleMap = mutableMapOf<MediaItem, AiringScheduleItem?>()
        mediaItemToAiringSchedulesMap.forEach { entry ->
            // map media item to closest airing schedule
            mediaToNextAiringScheduleMap[entry.key] = entry.value.reduceOrNull { first, second ->
                if (first.airingAt <= second.airingAt) {
                    first
                } else {
                    second
                }
            }
        }
        return mediaToNextAiringScheduleMap
    }
}