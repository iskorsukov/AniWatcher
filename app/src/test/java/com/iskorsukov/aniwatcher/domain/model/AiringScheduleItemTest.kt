package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.*
import com.iskorsukov.aniwatcher.data.entity.FollowingEntity
import org.junit.Test

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
}