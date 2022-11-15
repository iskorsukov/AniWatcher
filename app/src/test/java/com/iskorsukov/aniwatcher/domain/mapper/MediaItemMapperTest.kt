package com.iskorsukov.aniwatcher.domain.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import org.junit.Test

class MediaItemMapperTest {

    @Test
    fun groupAiringSchedulesByDayOfWeek() {
        val testData = mapOf(
            ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
        )

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
        val testData = mapOf(
            ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
        )

        val result = MediaItemMapper.groupMediaWithNextAiringSchedule(testData)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(result.values).containsExactly(ModelTestDataCreator.baseAiringScheduleItemList()[3])
    }
}