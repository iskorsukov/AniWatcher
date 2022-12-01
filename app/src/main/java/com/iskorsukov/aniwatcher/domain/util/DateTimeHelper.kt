package com.iskorsukov.aniwatcher.domain.util

import java.util.*

object DateTimeHelper {

    fun currentDayOfWeek(): DayOfWeekLocal {
        return DayOfWeekLocal.ofCalendar(Calendar.getInstance())
    }

    fun currentWeekStartToEndSeconds(calendar: Calendar): Pair<Int, Int> {
        val start = (calendar.timeInMillis / 1000).toInt()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val end = (calendar.timeInMillis / 1000).toInt()
        return start to end
    }

    fun currentSeasonYear(calendar: Calendar): SeasonYear {
        val currentSeason = Season.ofCalendarValue(calendar.get(Calendar.MONTH))
        val currentYear = calendar.get(Calendar.YEAR)
        return SeasonYear(
            currentSeason,
            if (currentSeason == Season.WINTER && calendar.get(Calendar.MONTH) == 11) currentYear + 1 else currentYear
        )
    }

    enum class Season {
        WINTER,
        SPRING,
        SUMMER,
        FALL;

        companion object {
            fun ofCalendarValue(value: Int): Season {
                return when (value) {
                    11, 0, 1 -> WINTER
                    2, 3, 4 -> SPRING
                    5, 6, 7 -> SUMMER
                    else -> FALL
                }
            }
        }
    }

    data class SeasonYear(
        val season: Season,
        val year: Int
    )
}