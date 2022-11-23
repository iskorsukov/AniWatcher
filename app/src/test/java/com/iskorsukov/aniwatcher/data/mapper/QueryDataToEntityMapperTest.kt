package com.iskorsukov.aniwatcher.data.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import com.iskorsukov.aniwatcher.test.nullAiringSchedule
import org.junit.Test

class QueryDataToEntityMapperTest {

    private val mapper = QueryDataToEntityMapper()

    @Test
    fun map() {
        val data = SeasonAiringDataQuery.Data(
            SeasonAiringDataQuery.Page(
                pageInfo = SeasonAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                media = listOf(QueryTestDataCreator.baseSeasonAiringDataMedium())
            )
        )

        val entityMap = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityMap.size).isEqualTo(1)
        assertThat(entityMap.keys).containsExactly(EntityTestDataCreator.baseMediaItemEntity())
        assertThat(entityMap.values.flatten()).containsExactlyElementsIn(
            EntityTestDataCreator.baseAiringScheduleEntityList()
        )

    }

    @Test(expected = IllegalArgumentException::class)
    fun map_hasNullValue() {
        val data = SeasonAiringDataQuery.Data(
            SeasonAiringDataQuery.Page(
                pageInfo = SeasonAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                media = listOf(QueryTestDataCreator.baseSeasonAiringDataMedium().nullAiringSchedule())
            )
        )

        mapper.mapMediaWithSchedulesList(data)
    }
}