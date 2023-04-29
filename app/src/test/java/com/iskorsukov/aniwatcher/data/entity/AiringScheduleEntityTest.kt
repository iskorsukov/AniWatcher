package com.iskorsukov.aniwatcher.data.entity

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.base.AiringScheduleEntity
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import org.junit.Test

class AiringScheduleEntityTest {

    @Test
    fun fromData_season() {
        val data = QueryTestDataCreator.seasonAiringDataScheduleNode(
            id = 1,
            airingAt = 1,
            episode = 1,
            mediaId = 1
        )

        val entity = AiringScheduleEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                airingAt = 1,
                episode = 1,
                mediaItemRelationId = 1
            )
        )
    }

    @Test
    fun fromData_range() {
        val data = QueryTestDataCreator.rangeAiringDataScheduleNode(
            id = 1,
            airingAt = 1,
            episode = 1,
            mediaId = 1
        )

        val entity = AiringScheduleEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                airingAt = 1,
                episode = 1,
                mediaItemRelationId = 1
            )
        )
    }
}