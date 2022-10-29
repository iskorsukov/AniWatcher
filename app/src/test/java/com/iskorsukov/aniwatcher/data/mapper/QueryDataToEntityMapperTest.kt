package com.iskorsukov.aniwatcher.data.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.EntityTestDataCreator
import com.iskorsukov.aniwatcher.QueryTestDataCreator
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.nullAiringSchedule
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

        val entityList = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityList.size).isEqualTo(1)
        assertThat(entityList[0]).isEqualTo(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
                .mediaItemWithAiringSchedulesEntity
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