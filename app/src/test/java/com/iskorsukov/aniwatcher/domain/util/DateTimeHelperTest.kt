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
    fun currentDayEndSeconds() {
        val dayEndSeconds = DateTimeHelper.currentDayEndSeconds(testCalendar)

        assertThat(dayEndSeconds).isEqualTo(TEST_DAY_END)
    }

    @Test
    fun currentWeekStartToEnd() {
        val startToEnd = DateTimeHelper.currentWeekStartToEndSeconds(testCalendar)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END)
    }

    @Test
    fun currentSeasonYear() {
        val inputCalendars = buildList<Calendar> {
            for (i in 0..11) {
                add((Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2022)
                    set(Calendar.MONTH, i)
                    set(Calendar.DAY_OF_MONTH, 10)
                }))
            }
        }

        val seasonOutputs = inputCalendars.map { DateTimeHelper.currentSeasonYear(it) }
        assertThat(seasonOutputs).containsExactlyElementsIn(
            listOf(
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SPRING, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SPRING, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SPRING, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SUMMER, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SUMMER, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.SUMMER, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022),
                DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023),
            )
        ).inOrder()
    }

    companion object {
        const val TEST_WEEK_START = 1666099271
        const val TEST_WEEK_END = 1666645200
        const val TEST_DAY_END = 1666126800
        val TEST_CALENDAR = GregorianCalendar(
            2022, Calendar.OCTOBER, 18, 16, 21, 11)
    }
}