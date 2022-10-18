package com.iskorsukov.aniwatcher.domain.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class WeekTimestampHelperTest {

    private val testCalendar = Calendar.getInstance().apply { time = TEST_CALENDAR.time }

    @Test
    fun currentWeekStartToEnd() {
        val startToEnd = WeekTimestampHelper.currentWeekStartToEndSeconds(testCalendar)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END)
    }

    @Test
    fun previousWeekStartToEnd() {
        val startToEnd = WeekTimestampHelper.weekStartToEndSeconds(testCalendar, -1)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START - SEVEN_DAYS_IN_SECONDS)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END - SEVEN_DAYS_IN_SECONDS)
    }

    @Test
    fun nextWeekStartToEnd() {
        val startToEnd = WeekTimestampHelper.weekStartToEndSeconds(testCalendar, 1)
        assertThat(startToEnd.first).isEqualTo(TEST_WEEK_START + SEVEN_DAYS_IN_SECONDS)
        assertThat(startToEnd.second).isEqualTo(TEST_WEEK_END + SEVEN_DAYS_IN_SECONDS)
    }

    companion object {
        val TEST_CALENDAR = GregorianCalendar(
            2022, Calendar.OCTOBER, 17, 16, 21, 11)
        const val TEST_WEEK_START = 1665954000
        const val TEST_WEEK_END = 1666558800
        const val SEVEN_DAYS_IN_SECONDS = 24 * 60 * 60 * 7
    }
}