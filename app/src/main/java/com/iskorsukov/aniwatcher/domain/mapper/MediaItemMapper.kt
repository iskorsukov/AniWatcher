package com.iskorsukov.aniwatcher.domain.mapper

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import java.util.*

object MediaItemMapper {

    fun filterExtraFollowedAiringSchedules(
        dowToAiringSchedulesMap: Map<DayOfWeekLocal, List<AiringScheduleItem>>,
        settingsState: SettingsState,
        selectedSeasonYear: DateTimeHelper.SeasonYear
    ): Map<DayOfWeekLocal, List<AiringScheduleItem>> {
        return dowToAiringSchedulesMap.mapValues { entry ->
            entry.value.filter { airingScheduleItem ->
                if (settingsState.scheduleType == ScheduleType.ALL) {
                    val startToEnd = DateTimeHelper.currentWeekStartToEndSeconds(Calendar.getInstance())
                    airingScheduleItem.airingAt in startToEnd.first..startToEnd.second
                } else {
                    airingScheduleItem.mediaItem.season == selectedSeasonYear.season.name && airingScheduleItem.mediaItem.year == selectedSeasonYear.year
                }
            }
        }.filter { it.value.isNotEmpty() }
    }

    fun filterExtraFollowedMedia(
        mediaItemToAiringScheduleMap: Map<MediaItem, AiringScheduleItem?>,
        settingsState: SettingsState,
        selectedSeasonYear: DateTimeHelper.SeasonYear
    ): Map<MediaItem, AiringScheduleItem?> {
        return mediaItemToAiringScheduleMap.filter { entry ->
            if (settingsState.scheduleType == ScheduleType.ALL) {
                val startToEnd = DateTimeHelper.currentWeekStartToEndSeconds(Calendar.getInstance())
                entry.value != null && entry.value!!.airingAt in startToEnd.first..startToEnd.second
            } else {
                entry.key.season == selectedSeasonYear.season.name && entry.key.year == selectedSeasonYear.year
            }
        }
    }

    fun groupAiringSchedulesByDayOfWeek(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>
    ): Map<DayOfWeekLocal, List<AiringScheduleItem>> {
        val dowToSchedulesMap = mutableMapOf<DayOfWeekLocal, List<AiringScheduleItem>>()
        mediaItemToAiringSchedulesMap.mapNotNull {
            val map = mutableMapOf<DayOfWeekLocal, AiringScheduleItem>()
            val currentDayOfWeek = DayOfWeekLocal.ofCalendar(Calendar.getInstance())
            val currentDayEndInSeconds = DateTimeHelper.currentDayEndSeconds(Calendar.getInstance())
            it.value.forEach { item ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = item.airingAt.toLong() * 1000
                }
                val dayOfWeek = DayOfWeekLocal.ofCalendar(calendar)
                if (dayOfWeek == currentDayOfWeek && item.airingAt > currentDayEndInSeconds)
                    return@forEach
                if (item.airingAt < (map[dayOfWeek]?.airingAt ?: Int.MAX_VALUE)) {
                    map[dayOfWeek] = item
                }
            }
            map.forEach { (dayOfWeek, item) ->
                val list = dowToSchedulesMap[dayOfWeek]
                val newList = mutableListOf<AiringScheduleItem>().apply {
                    if (list != null) addAll(list)
                    add(item)
                }
                newList.sortBy { scheduleItem -> scheduleItem.airingAt }
                dowToSchedulesMap[dayOfWeek] = newList
            }
        }
        return dowToSchedulesMap
    }

    fun groupMediaWithNextAiringSchedule(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>
    ): Map<MediaItem, AiringScheduleItem?> {
        val map = mutableMapOf<MediaItem, AiringScheduleItem?>()
        mediaItemToAiringSchedulesMap.forEach {
            map[it.key] = it.value.reduceOrNull { first, second ->
                if (first.airingAt < second.airingAt) {
                    first
                } else {
                    second
                }
            }
        }
        return map
    }
}