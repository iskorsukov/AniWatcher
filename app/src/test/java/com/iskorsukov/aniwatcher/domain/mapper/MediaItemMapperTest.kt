package com.iskorsukov.aniwatcher.domain.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.Test

class MediaItemMapperTest {

    private val mapper = MediaItemMapper()

    @Test
    fun groupAiringSchedulesByDayOfWeek() {
        mockkObject(DayOfWeekLocal.Companion)
        every {
            DayOfWeekLocal.Companion.ofCalendar(any())
        } returnsMany listOf(
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.TUESDAY,
            DayOfWeekLocal.SATURDAY
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.SUNDAY
        every { DateTimeHelper.currentDayEndSeconds(any()) } returns 60

        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 65
                )
            )
        )
        val testDataTuesdayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 2),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 2,
                    airingAt = 70
                )
            )
        )
        val testDataSaturdayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 3),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 3,
                    airingAt = 90
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair,
            testDataTuesdayPair,
            testDataSaturdayPair
        )

        val result = mapper.groupAiringSchedulesByDayOfWeek(testData, 0)

        assertThat(result.size).isEqualTo(3)
        assertThat(result.keys).containsExactly(
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.TUESDAY,
            DayOfWeekLocal.SATURDAY
        )
        assertThat(result[DayOfWeekLocal.MONDAY]).isEqualTo(
            listOf(
                Pair(testDataMondayPair.second[0], testDataMondayPair.first)
            )
        )
        assertThat(result[DayOfWeekLocal.TUESDAY]).isEqualTo(
            listOf(
                Pair(testDataTuesdayPair.second[0], testDataTuesdayPair.first)
            )
        )
        assertThat(result[DayOfWeekLocal.SATURDAY]).isEqualTo(
            listOf(
                Pair(testDataSaturdayPair.second[0], testDataSaturdayPair.first)
            )
        )

        unmockkAll()
    }

    @Test
    fun groupAiringSchedulesByDayOfWeek_filtersNextWeekItem() {
        mockkObject(DayOfWeekLocal.Companion)
        every {
            DayOfWeekLocal.Companion.ofCalendar(any())
        } returnsMany listOf(
            DayOfWeekLocal.MONDAY
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.MONDAY
        every { DateTimeHelper.currentDayEndSeconds(any()) } returns 60

        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 65
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair
        )

        val result = mapper.groupAiringSchedulesByDayOfWeek(testData, 0)

        assertThat(result).isEmpty()

        unmockkAll()
    }

    @Test
    fun groupAiringSchedulesByDayOfWeek_filtersPastItem() {
        mockkObject(DayOfWeekLocal.Companion)
        every {
            DayOfWeekLocal.Companion.ofCalendar(any())
        } returnsMany listOf(
            DayOfWeekLocal.MONDAY
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.MONDAY
        every { DateTimeHelper.currentDayEndSeconds(any()) } returns 60

        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 75
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair
        )

        val result = mapper.groupAiringSchedulesByDayOfWeek(testData, 2)

        assertThat(result).isEmpty()

        unmockkAll()
    }

    @Test
    fun groupAiringSchedulesByDayOfWeek_choosesOnlyOneSchedulePerMedia() {
        mockkObject(DayOfWeekLocal.Companion)
        every {
            DayOfWeekLocal.Companion.ofCalendar(any())
        } returnsMany listOf(
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.MONDAY
        )

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.SUNDAY
        every { DateTimeHelper.currentDayEndSeconds(any()) } returns 60

        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 85
                ),
                ModelTestDataCreator.airingScheduleItem(
                    id = 2,
                    airingAt = 75
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair
        )

        val result = mapper.groupAiringSchedulesByDayOfWeek(testData, 0)

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            DayOfWeekLocal.MONDAY
        )
        assertThat(result[DayOfWeekLocal.MONDAY]).isEqualTo(
            listOf(
                Pair(testDataMondayPair.second[1], testDataMondayPair.first)
            )
        )

        unmockkAll()
    }

    @Test
    fun groupMediaWithNextAiringSchedule() {
        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 85
                ),
                ModelTestDataCreator.airingScheduleItem(
                    id = 2,
                    airingAt = 75
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair
        )

        val result = mapper.groupMediaWithNextAiringSchedule(testData, 1)

        assertThat(result.size).isEqualTo(1)
        assertThat(result.values).containsExactly(
            testDataMondayPair.second[1]
        )
    }

    @Test
    fun groupMediaWithNextAiringSchedule_filtersPastItem() {
        val testDataMondayPair = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            listOf(
                ModelTestDataCreator.airingScheduleItem(
                    id = 1,
                    airingAt = 55
                )
            )
        )
        val testData = mapOf(
            testDataMondayPair
        )

        val result = mapper.groupMediaWithNextAiringSchedule(testData, 1)

        assertThat(result.size).isEqualTo(1)
        assertThat(result[testDataMondayPair.first]).isNull()
    }
}