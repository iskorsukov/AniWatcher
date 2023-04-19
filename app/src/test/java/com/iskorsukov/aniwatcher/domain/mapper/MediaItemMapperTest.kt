package com.iskorsukov.aniwatcher.domain.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import org.junit.Test

class MediaItemMapperTest {

    private val testData = mapOf(
        ModelTestDataCreator.baseMediaItem to ModelTestDataCreator.baseAiringScheduleItemList()
    )

    private val testDataEmptySchedules = mapOf(
        ModelTestDataCreator.baseMediaItem to emptyList<AiringScheduleItem>()
    )

    private val mapper = MediaItemMapper()

    @Test
    fun groupAiringSchedulesByDayOfWeek() {
        val result = mapper.groupAiringSchedulesByDayOfWeek(testData, ModelTestDataCreator.TIME_IN_MINUTES)

        assertThat(result.keys).containsExactlyElementsIn(listOf(
            DayOfWeekLocal.FRIDAY,
            DayOfWeekLocal.SATURDAY,
            DayOfWeekLocal.WEDNESDAY
        ))

        val list = ModelTestDataCreator.baseAiringScheduleToMediaPairList()
        val assertValues = listOf(
            list[0],
            list[1],
            list[2]
        )
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues)
    }

    @Test
    fun groupMediaWithNextAiringSchedule() {
        val result = mapper.groupMediaWithNextAiringSchedule(testData, ModelTestDataCreator.TIME_IN_MINUTES)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem)
        assertThat(result.values).containsExactly(ModelTestDataCreator.baseAiringScheduleItemList().first())
    }

    @Test
    fun groupMediaWithNextAiringSchedule_null() {
        val result = mapper.groupMediaWithNextAiringSchedule(testDataEmptySchedules, ModelTestDataCreator.TIME_IN_MINUTES)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem)
        assertThat(result.values).containsExactly(null)
    }
}