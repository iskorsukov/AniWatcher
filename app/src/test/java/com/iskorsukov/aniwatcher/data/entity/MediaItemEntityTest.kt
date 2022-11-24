package com.iskorsukov.aniwatcher.data.entity

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.test.*
import org.junit.Test

class MediaItemEntityTest {

    @Test
    fun fromData() {
        val data = QueryTestDataCreator.baseSeasonAiringDataMedium()

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(EntityTestDataCreator.baseMediaItemEntity())
    }
}