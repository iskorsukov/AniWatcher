package com.iskorsukov.aniwatcher.domain.util

import java.util.*

object DateTimeHelper {

    fun currentDayOfWeek(): DayOfWeekLocal {
        return DayOfWeekLocal.ofCalendar(Calendar.getInstance())
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