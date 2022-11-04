package com.iskorsukov.aniwatcher.domain.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Calendar

class DayOfWeekLocalTest {

    @Test
    fun ofCalendar() {
        val inputValues = listOf(
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 1)
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 2)
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 3)
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 4)
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 5)
            },
            Calendar.getInstance().apply {
                timeInMillis = 1667558660L * 1000
                add(Calendar.DAY_OF_YEAR, 6)
            },
        )

        val outputValues = inputValues.map { DayOfWeekLocal.ofCalendar(it) }

        assertThat(outputValues).containsExactlyElementsIn(
            listOf(
                DayOfWeekLocal.FRIDAY,
                DayOfWeekLocal.SATURDAY,
                DayOfWeekLocal.SUNDAY,
                DayOfWeekLocal.MONDAY,
                DayOfWeekLocal.TUESDAY,
                DayOfWeekLocal.WEDNESDAY,
                DayOfWeekLocal.THURSDAY,

            )
        )
    }
}