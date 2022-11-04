package com.iskorsukov.aniwatcher.domain.util

import java.util.Calendar

enum class DayOfWeekLocal {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    companion object {
        fun ofCalendar(calendar: Calendar): DayOfWeekLocal {
            return when(calendar.get(Calendar.DAY_OF_WEEK)) {
                1 -> SUNDAY
                2 -> MONDAY
                3 -> TUESDAY
                4 -> WEDNESDAY
                5 -> THURSDAY
                6 -> FRIDAY
                7 -> SATURDAY
                else -> MONDAY
            }
        }
    }
}