package com.iskorsukov.aniwatcher.domain.util

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import java.util.*

class DateTimeHelperTest {

    private val testCalendar = Calendar.getInstance().apply { time = TEST_CALENDAR.time }

    @Test
    fun currentDayOfWeek() {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        val dayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

        assertThat(dayOfWeekLocal).isEqualTo(DayOfWeekLocal.WEDNESDAY)

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun currentWeekStartToEnd() {
        val startToEnd = DateTimeHelper.currentWeekStartToEndSeconds(testCalendar)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END)
    }

    @Test
    fun previousWeekStartToEnd() {
        val startToEnd = DateTimeHelper.weekStartToEndSeconds(testCalendar, -1)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START - SEVEN_DAYS_IN_SECONDS)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END - SEVEN_DAYS_IN_SECONDS)
    }

    @Test
    fun nextWeekStartToEnd() {
        val startToEnd = DateTimeHelper.weekStartToEndSeconds(testCalendar, 1)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START + SEVEN_DAYS_IN_SECONDS)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END + SEVEN_DAYS_IN_SECONDS)
    }

    @Test
    fun currentSeason() {
        val inputCalendars = buildList<Calendar> {
            for (i in 0..11) {
                add((Calendar.getInstance().apply {
                    set(Calendar.MONTH, i)
                    set(Calendar.DAY_OF_MONTH, 10)
                }))
            }
        }

        val seasonOutputs = inputCalendars.map { DateTimeHelper.currentSeason(it) }
        assertThat(seasonOutputs).containsExactlyElementsIn(
            listOf(
                "WINTER",
                "WINTER",
                "SPRING",
                "SPRING",
                "SPRING",
                "SUMMER",
                "SUMMER",
                "SUMMER",
                "FALL",
                "FALL",
                "FALL",
                "WINTER"
            )
        ).inOrder()
    }

    @Test
    fun currentYear() {
        assertThat(DateTimeHelper.currentYear(testCalendar)).isEqualTo(2022)
    }

    companion object {
        val TEST_CALENDAR = GregorianCalendar(
            2022, Calendar.OCTOBER, 17, 16, 21, 11)
        const val TEST_WEEK_START = 1665954000
        const val TEST_WEEK_END = 1666558800
        const val SEVEN_DAYS_IN_SECONDS = 24 * 60 * 60 * 7
    }
}