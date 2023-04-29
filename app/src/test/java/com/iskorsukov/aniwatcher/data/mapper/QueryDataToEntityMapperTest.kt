package com.iskorsukov.aniwatcher.data.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
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
                media = listOf(
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 1,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            listOf(
                                QueryTestDataCreator.seasonAiringDataScheduleNode(
                                    id = 1,
                                    mediaId = 1
                                )
                            )
                        )
                    ),
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 2,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            listOf(
                                QueryTestDataCreator.seasonAiringDataScheduleNode(
                                    id = 2,
                                    mediaId = 2
                                ),
                                QueryTestDataCreator.seasonAiringDataScheduleNode(
                                    id = 3,
                                    mediaId = 2
                                )
                            )
                        )
                    )
                )
            )
        )

        val entityMap = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityMap.size).isEqualTo(2)
        assertThat(entityMap.keys).containsExactly(
            EntityTestDataCreator.mediaItemEntity(mediaId = 1),
            EntityTestDataCreator.mediaItemEntity(mediaId = 2)
        )
        assertThat(entityMap.values.flatten()).containsExactly(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            ),
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 2,
                mediaItemRelationId = 2
            ),
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 3,
                mediaItemRelationId = 2
            )
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
                media = listOf(
                    QueryTestDataCreator.seasonAiringDataMedium(id = 1)
                )
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
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 1,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                listOf(
                                    QueryTestDataCreator.rangeAiringDataScheduleNode(
                                        id = 1,
                                        mediaId = 1
                                    )
                                )
                            )
                        )
                    ),
                    RangeAiringDataQuery.AiringSchedule(
                        id = 2,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 2,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                listOf(
                                    QueryTestDataCreator.rangeAiringDataScheduleNode(
                                        id = 2,
                                        mediaId = 2
                                    ),
                                    QueryTestDataCreator.rangeAiringDataScheduleNode(
                                        id = 3,
                                        mediaId = 2
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val entityMap = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityMap.size).isEqualTo(2)
        assertThat(entityMap.keys).containsExactly(
            EntityTestDataCreator.mediaItemEntity(mediaId = 1),
            EntityTestDataCreator.mediaItemEntity(mediaId = 2)
        )
        assertThat(entityMap.values.flatten()).containsExactly(
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 1,
                mediaItemRelationId = 1
            ),
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 2,
                mediaItemRelationId = 2
            ),
            EntityTestDataCreator.airingScheduleEntity(
                airingScheduleEntityId = 3,
                mediaItemRelationId = 2
            )
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
                        media = QueryTestDataCreator.rangeAiringDataMedia(id = 1)
                    )
                )
            )
        )

        mapper.mapMediaWithSchedulesList(data)
    }

    @Test
    fun mapRange_filtersAdultMedia() {
        val data = RangeAiringDataQuery.Data(
            RangeAiringDataQuery.Page(
                pageInfo = RangeAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                airingSchedules = listOf(
                    RangeAiringDataQuery.AiringSchedule(
                        id = 1,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 1,
                            isAdult = true,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                listOf(
                                    QueryTestDataCreator.rangeAiringDataScheduleNode(
                                        id = 1,
                                        mediaId = 1
                                    )
                                )
                            )
                        )
                    ),
                    RangeAiringDataQuery.AiringSchedule(
                        id = 2,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 2,
                            isAdult = null,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                listOf(
                                    QueryTestDataCreator.rangeAiringDataScheduleNode(
                                        id = 2,
                                        mediaId = 2
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val entityMap = mapper.mapMediaWithSchedulesList(data)

        assertThat(entityMap).isEmpty()
    }

}