package com.iskorsukov.aniwatcher.domain.util

import java.util.*

object DateTimeHelper {

    fun currentWeekStartToEndSeconds(calendar: Calendar): Pair<Int, Int> {
        val dayOfWeekDelta = calendar.get(Calendar.DAY_OF_WEEK) - 2 // -2 to account for start from Sunday and Sunday = 1
        calendar.apply {
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR_OF_DAY, 0)
        }
        calendar.add(Calendar.DAY_OF_YEAR, -dayOfWeekDelta)
        val start = (calendar.timeInMillis / 1000).toInt()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val end = (calendar.timeInMillis / 1000).toInt()
        return start to end
    }

    fun weekStartToEndSeconds(calendar: Calendar, weekOffset: Int): Pair<Int, Int> {
        calendar.add(Calendar.DAY_OF_YEAR, 7 * weekOffset)
        return currentWeekStartToEndSeconds(calendar)
    }

    fun currentYear(calendar: Calendar): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun currentSeason(calendar: Calendar): String {
        return when (calendar.get(Calendar.MONTH)) {
            11, 0, 1 -> "WINTER"
            2, 3, 4 -> "SPRING"
            5, 6, 7 -> "SUMMER"
            else -> "FALL"
        }
    }
}