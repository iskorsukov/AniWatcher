package com.iskorsukov.aniwatcher.domain.mapper

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaItemMapper @Inject constructor() {

    fun groupAiringSchedulesByDayOfWeek(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>,
        timeInMinutes: Long
    ): Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>> {
        val dayOfWeekSchedulesMap =
            mutableMapOf<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>>()
        mediaItemToAiringSchedulesMap.forEach { entry ->
            val currentDayOfWeek = DayOfWeekLocal.ofCalendar(Calendar.getInstance())
            val currentDayEndInSeconds = DateTimeHelper.currentDayEndSeconds(Calendar.getInstance())

            // map media entry schedules to days of week
            val dayOfWeekSchedulesForEntryMap =
                mutableMapOf<DayOfWeekLocal, Pair<AiringScheduleItem, MediaItem>>()
            entry.value
                .filter { schedule -> schedule.airingAt > TimeUnit.MINUTES.toSeconds(timeInMinutes) }
                .forEach inner@{ airingSchedule ->
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = airingSchedule.airingAt.toLong() * 1000
                    }
                    val dayOfWeek = DayOfWeekLocal.ofCalendar(calendar)
                    // for current day of week show only schedules that air today, not a week or more away
                    // this mostly helps visual clutter
                    if (dayOfWeek == currentDayOfWeek && airingSchedule.airingAt > currentDayEndInSeconds)
                        return@inner
                    // save closes airing item for each day of week
                    // usually there will be only one, but this allows to filter occasional stack
                    // of multiple episodes airing on the same day
                    if (airingSchedule.airingAt < (dayOfWeekSchedulesForEntryMap[dayOfWeek]?.first?.airingAt
                            ?: Int.MAX_VALUE)
                    ) {
                        dayOfWeekSchedulesForEntryMap[dayOfWeek] = airingSchedule to entry.key
                    }
                }

            // add grouped schedules of entry to global day of week map
            dayOfWeekSchedulesForEntryMap.forEach { (dayOfWeek, item) ->
                val globalDayOfWeekSchedules = dayOfWeekSchedulesMap[dayOfWeek]
                val updatedGlobalDayOfWeekSchedules =
                    mutableListOf<Pair<AiringScheduleItem, MediaItem>>().apply {
                        if (globalDayOfWeekSchedules != null) addAll(globalDayOfWeekSchedules)
                        add(item)
                    }
                updatedGlobalDayOfWeekSchedules.sortBy { scheduleItemPair -> scheduleItemPair.first.airingAt }
                dayOfWeekSchedulesMap[dayOfWeek] = updatedGlobalDayOfWeekSchedules
            }
        }
        return dayOfWeekSchedulesMap
    }

    fun groupMediaWithNextAiringSchedule(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>,
        timeInMinutes: Long
    ): Map<MediaItem, AiringScheduleItem?> {
        val mediaToNextAiringScheduleMap = mutableMapOf<MediaItem, AiringScheduleItem?>()
        mediaItemToAiringSchedulesMap.forEach { entry ->
            // map media item to closest airing schedule
            mediaToNextAiringScheduleMap[entry.key] = entry.value
                .filter { schedule -> schedule.airingAt > TimeUnit.MINUTES.toSeconds(timeInMinutes) }
                .reduceOrNull { first, second ->
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