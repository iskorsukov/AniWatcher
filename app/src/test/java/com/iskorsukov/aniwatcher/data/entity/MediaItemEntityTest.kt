package com.iskorsukov.aniwatcher.data.entity

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.EntityTestDataCreator
import com.iskorsukov.aniwatcher.QueryTestDataCreator
import com.iskorsukov.aniwatcher.emptyGenres
import com.iskorsukov.aniwatcher.nullGenres
import org.junit.Test

class MediaItemEntityTest {

    @Test
    fun fromData() {
        val data = QueryTestDataCreator.baseSeasonAiringDataMedium()

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(EntityTestDataCreator.baseMediaItemEntity())
    }

    @Test
    fun fromData_nullGenres() {
        val data = QueryTestDataCreator.baseSeasonAiringDataMedium().nullGenres()

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(EntityTestDataCreator.baseMediaItemEntity().nullGenres())
    }

    @Test
    fun fromData_emptyGenres() {
        val data = QueryTestDataCreator.baseSeasonAiringDataMedium().emptyGenres()

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(EntityTestDataCreator.baseMediaItemEntity().emptyGenres())
    }
}