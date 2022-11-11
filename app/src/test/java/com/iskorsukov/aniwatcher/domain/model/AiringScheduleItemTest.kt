package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import com.iskorsukov.aniwatcher.test.*
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class AiringScheduleItemTest {

    @Test
    fun fromEntity() {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()

        val airingScheduleItemList = AiringScheduleItem.fromEntity(entity)

        assertThat(airingScheduleItemList).containsExactlyElementsIn(
            ModelTestDataCreator.baseAiringScheduleItemList()
        )
    }

    @Test
    fun fromEntity_withFollowing() {
        val entity = EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity().followingEntity(
            FollowingEntity(1, 1)
        )

        val airingScheduleItemList = AiringScheduleItem.fromEntity(entity)

        assertThat(airingScheduleItemList).containsExactlyElementsIn(
            ModelTestDataCreator.baseAiringScheduleItemList().map { it.mediaItem(it.mediaItem.isFollowing(true)) }
        )
    }

    @Test
    fun getAiringAtFormatted() {
        Locale.setDefault(Locale.US)

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringAtDateTimeFormatted()).isEqualTo("November 07, 18:00")
    }

    @Test
    fun getAiringInFormatted() {
        val testMinutes = TimeUnit.SECONDS.toMinutes(1667726120L)

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 5 hours, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noDays() {
        val testMinutes = TimeUnit.SECONDS.toMinutes(1667812520L)

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("5 hours, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noHours() {
        val testMinutes = TimeUnit.SECONDS.toMinutes(1667744120L)

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noMinutes() {
        val testMinutes = TimeUnit.SECONDS.toMinutes(1667725220L)

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 6 hours")
    }

    @Test
    fun getAiringInFormatted_null() {
        val testMinutes = Long.MAX_VALUE

        val airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem()

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isNull()
    }
}