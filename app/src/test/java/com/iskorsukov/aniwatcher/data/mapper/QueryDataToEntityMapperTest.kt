package com.iskorsukov.aniwatcher.data.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import com.iskorsukov.aniwatcher.test.nullAiringSchedule
import com.iskorsukov.aniwatcher.test.seasonRanking
import org.junit.Test

class QueryDataToEntityMapperTest {

    private val mapper = QueryDataToEntityMapper()

    @Test
    fun mapSeason() {
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
    fun mapSeason_hasNullValue() {
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

    @Test
    fun mapRange() {
        val data = RangeAiringDataQuery.Data(
            RangeAiringDataQuery.Page(
                pageInfo = RangeAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                airingSchedules = listOf(
                    RangeAiringDataQuery.AiringSchedule(
                        id = 1,
                        media = QueryTestDataCreator.baseRangeAiringDataMedium()
                    )
                )
            )
        )

        val entityMap = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityMap.size).isEqualTo(1)
        assertThat(entityMap.keys).containsExactly(
            EntityTestDataCreator.baseMediaItemEntity().seasonRanking(null)
        )
        assertThat(entityMap.values.flatten()).containsExactlyElementsIn(
            EntityTestDataCreator.baseAiringScheduleEntityList()
        )

    }

    @Test(expected = IllegalArgumentException::class)
    fun mapRange_hasNullValue() {
        val data = RangeAiringDataQuery.Data(
            RangeAiringDataQuery.Page(
                pageInfo = RangeAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                airingSchedules = listOf(
                    RangeAiringDataQuery.AiringSchedule(
                        id = 1,
                        media = QueryTestDataCreator.baseRangeAiringDataMedium()
                            .nullAiringSchedule()
                    )
                )
            )
        )

        mapper.mapMediaWithSchedulesList(data)
    }
}