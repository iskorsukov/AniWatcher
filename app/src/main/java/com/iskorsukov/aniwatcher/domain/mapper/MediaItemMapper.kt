package com.iskorsukov.aniwatcher.domain.mapper

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import java.util.*

object MediaItemMapper {

    fun groupAiringSchedulesByDayOfWeek(
        mediaItemToAiringSchedulesMap: Map<MediaItem, List<AiringScheduleItem>>
    ): Map<DayOfWeekLocal, List<AiringScheduleItem>> {
        val dowToSchedulesMap = mutableMapOf<DayOfWeekLocal, List<AiringScheduleItem>>()
        mediaItemToAiringSchedulesMap.mapNotNull {
            val map = mutableMapOf<DayOfWeekLocal, AiringScheduleItem>()
            it.value.forEach { item ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = item.airingAt.toLong() * 1000
                }
                val dayOfWeek = DayOfWeekLocal.ofCalendar(calendar)
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