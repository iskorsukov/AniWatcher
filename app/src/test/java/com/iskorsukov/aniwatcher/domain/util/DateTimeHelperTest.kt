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
    }
}