package com.iskorsukov.aniwatcher.data.entity

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.EntityTestDataCreator
import com.iskorsukov.aniwatcher.QueryTestDataCreator
import org.junit.Test

class AiringScheduleEntityTest {

    @Test
    fun fromData() {
        val data = QueryTestDataCreator.baseSeasonAiringDataNode()

        val entity = AiringScheduleEntity.fromData(data)

        assertThat(entity).isEqualTo(EntityTestDataCreator.baseAiringScheduleEntity())
    }
}