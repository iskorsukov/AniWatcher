package com.iskorsukov.aniwatcher.domain.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import org.junit.Test

class MediaItemMapperTest {

    private val testData = mapOf(
        ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
    )

    private val testDataEmptySchedules = mapOf(
        ModelTestDataCreator.baseMediaItem() to emptyList<AiringScheduleItem>()
    )

    @Test
    fun groupAiringSchedulesByDayOfWeek() {
        val result = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testData)

        assertThat(result.keys).containsExactlyElementsIn(listOf(
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.WEDNESDAY,
            DayOfWeekLocal.SATURDAY
        ))

        val list = ModelTestDataCreator.baseAiringScheduleItemList()
        val assertValues = listOf(
            list[0],
            list[1],
            list[3]
        )
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues)
    }

    @Test
    fun groupMediaWithNextAiringSchedule() {
        val result = MediaItemMapper.groupMediaWithNextAiringSchedule(testData)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(result.values).containsExactly(ModelTestDataCreator.baseAiringScheduleItemList()[3])
    }

    @Test
    fun groupMediaWithNextAiringSchedule_null() {
        val result = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataEmptySchedules)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(result.values).containsExactly(null)
    }
}