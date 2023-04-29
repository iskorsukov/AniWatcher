package com.iskorsukov.aniwatcher.domain.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.executor.PersistentMediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import com.iskorsukov.aniwatcher.domain.exception.ApolloException
import com.iskorsukov.aniwatcher.domain.exception.RoomException
import com.iskorsukov.aniwatcher.domain.util.LocalClockSystem
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AiringRepositoryTest {
    private val aniListQueryExecutor: AniListQueryExecutor = mockk(relaxed = true)
    private val mapper: QueryDataToEntityMapper = mockk(relaxed = true)
    private val mediaDatabaseExecutor: MediaDatabaseExecutor = mockk(relaxed = true)
    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor = mockk(relaxed = true)
    private val clock: LocalClockSystem = mockk<LocalClockSystem>(relaxed = true).also {
        coEvery { it.currentTimeMillis() } returns 0
    }

    private val season = "FALL"
    private val year = 2022
    private val page = 1
    private val start = 0
    private val end = 1

    private lateinit var repository: AiringRepositoryImpl

    private val mediaFlowPair =
        Pair(
            EntityTestDataCreator.mediaItemEntity(mediaId = 1),
            listOf(
                EntityTestDataCreator.airingScheduleEntity(
                    airingScheduleEntityId = 1,
                    mediaItemRelationId = 1
                )
            )
        )
    private val mediaFlowMap = mapOf(mediaFlowPair)

    @Test
    fun mediaWithSchedulesFlow() = runTest {
        coEvery { mediaDatabaseExecutor.mediaDataFlow } returns flowOf(mediaFlowMap)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(emptyMap())
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.mediaWithSchedulesFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(ModelTestDataCreator.mediaItem(id = 1))
        assertThat(model.values.flatten()).containsExactly(
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun mediaWithSchedulesFlow_mediaFollowed() = runTest {
        coEvery { mediaDatabaseExecutor.mediaDataFlow } returns flowOf(mediaFlowMap)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.mediaWithSchedulesFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(
            ModelTestDataCreator.mediaItem(
                id = 1,
                isFollowing = true
            )
        )
        assertThat(model.values.flatten()).containsExactly(
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun followedMediaFlow() = runTest {
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.followedMediaFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(
            ModelTestDataCreator.mediaItem(id = 1, isFollowing = true)
        )
        assertThat(model.values.flatten()).containsExactly(
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun getMediaWithAiringSchedules() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedules(any()) } returns flowOf(mediaFlowPair)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(emptyMap())
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.getMediaWithAiringSchedules(1).first()

        assertThat(model).isNotNull()
        assertThat(model!!.first).isEqualTo(ModelTestDataCreator.mediaItem(id = 1))
        assertThat(model.second).containsExactly(
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )

        coVerify {
            mediaDatabaseExecutor.getMediaWithAiringSchedules(1)
        }
    }

    @Test
    fun getMediaWithAiringSchedules_onlyFollowed() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedules(any()) } returns flowOf(null)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.getMediaWithAiringSchedules(1).first()

        assertThat(model).isNotNull()
        assertThat(model!!.first).isEqualTo(
            ModelTestDataCreator.mediaItem(id = 1, isFollowing = true)
        )
        assertThat(model.second).containsExactly(
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )

        coVerify {
            mediaDatabaseExecutor.getMediaWithAiringSchedules(1)
        }
    }

    @Test
    fun loadSeasonAiringData() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test
    fun loadSeasonAiringData_multiplePages() = runTest {
        coEvery {
            aniListQueryExecutor.seasonAiringDataQuery(any(), any(), 1)
        } returns SeasonAiringDataQuery.Data(
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
                    )
                )
            )
        )
        coEvery {
            aniListQueryExecutor.seasonAiringDataQuery(any(), any(), 2)
        } returns SeasonAiringDataQuery.Data(
            SeasonAiringDataQuery.Page(
                pageInfo = SeasonAiringDataQuery.PageInfo(
                    currentPage = 2,
                    hasNextPage = false
                ),
                media = listOf(
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 2,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            listOf(
                                QueryTestDataCreator.seasonAiringDataScheduleNode(
                                    id = 2,
                                    mediaId = 2
                                )
                            )
                        )
                    )
                )
            )
        )
        repository = AiringRepositoryImpl(
            aniListQueryExecutor,
            QueryDataToEntityMapper(),
            mediaDatabaseExecutor,
            persistentMediaDatabaseExecutor,
            clock
        )

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, 1)
            aniListQueryExecutor.seasonAiringDataQuery(year, season, 2)
            mediaDatabaseExecutor.updateMedia(
                mapOf(
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(mediaId = 1, popularity = 1),
                        listOf(
                            EntityTestDataCreator.airingScheduleEntity(
                                airingScheduleEntityId = 1,
                                mediaItemRelationId = 1
                            )
                        )
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(mediaId = 2, popularity = 2),
                        listOf(
                            EntityTestDataCreator.airingScheduleEntity(
                                airingScheduleEntityId = 2,
                                mediaItemRelationId = 2
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    fun loadSeasonAiringData_reassignPopularity() = runTest {
        coEvery {
            aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any())
        } returns SeasonAiringDataQuery.Data(
            SeasonAiringDataQuery.Page(
                pageInfo = SeasonAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = false
                ),
                media = listOf(
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 1,
                        popularity = 15,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            emptyList()
                        )
                    ),
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 2,
                        popularity = 35,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            emptyList()
                        )
                    ),
                    QueryTestDataCreator.seasonAiringDataMedium(
                        id = 3,
                        popularity = 25,
                        airingSchedule = QueryTestDataCreator.seasonAiringDataSchedule(
                            emptyList()
                        )
                    )
                )
            )
        )
        repository = AiringRepositoryImpl(
            aniListQueryExecutor,
            QueryDataToEntityMapper(),
            mediaDatabaseExecutor,
            persistentMediaDatabaseExecutor,
            clock
        )

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mediaDatabaseExecutor.updateMedia(
                mapOf(
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 2,
                            popularity = 1
                        ),
                        emptyList()
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 3,
                            popularity = 2
                        ),
                        emptyList()
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 1,
                            popularity = 3
                        ),
                        emptyList()
                    )
                )
            )
        }
    }

    @Test(expected = ApolloException::class)
    fun loadSeasonAiringData_queryException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        }
    }

    @Test(expected = ApolloException::class)
    fun loadSeasonAiringData_mapperException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>()) } throws IllegalArgumentException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>())
        }
    }

    @Test(expected = RoomException::class)
    fun loadSeasonAiringData_databaseException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { mediaDatabaseExecutor.updateMedia(any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test
    fun loadRangeAiringData() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }


    @Test
    fun loadRangeAiringData_multiplePages() = runTest {
        coEvery {
            aniListQueryExecutor.rangeAiringDataQuery(any(), any(), 1)
        } returns RangeAiringDataQuery.Data(
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
                    )
                )
            )
        )
        coEvery {
            aniListQueryExecutor.rangeAiringDataQuery(any(), any(), 2)
        } returns RangeAiringDataQuery.Data(
            RangeAiringDataQuery.Page(
                pageInfo = RangeAiringDataQuery.PageInfo(
                    currentPage = 2,
                    hasNextPage = false
                ),
                airingSchedules = listOf(
                    RangeAiringDataQuery.AiringSchedule(
                        id = 2,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 2,
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
        repository = AiringRepositoryImpl(
            aniListQueryExecutor,
            QueryDataToEntityMapper(),
            mediaDatabaseExecutor,
            persistentMediaDatabaseExecutor,
            clock
        )

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, 1)
            aniListQueryExecutor.rangeAiringDataQuery(start, end, 2)
            mediaDatabaseExecutor.updateMedia(
                mapOf(
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(mediaId = 1, popularity = 1),
                        listOf(
                            EntityTestDataCreator.airingScheduleEntity(
                                airingScheduleEntityId = 1,
                                mediaItemRelationId = 1
                            )
                        )
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(mediaId = 2, popularity = 2),
                        listOf(
                            EntityTestDataCreator.airingScheduleEntity(
                                airingScheduleEntityId = 2,
                                mediaItemRelationId = 2
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    fun loadRangeAiringData_reassignPopularity() = runTest {
        coEvery {
            aniListQueryExecutor.rangeAiringDataQuery(any(), any(), any())
        } returns RangeAiringDataQuery.Data(
            RangeAiringDataQuery.Page(
                pageInfo = RangeAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = false
                ),
                airingSchedules = listOf(
                    RangeAiringDataQuery.AiringSchedule(
                        id = 1,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 1,
                            popularity = 15,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                emptyList()
                            )
                        )
                    ),
                    RangeAiringDataQuery.AiringSchedule(
                        id = 2,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 2,
                            popularity = 35,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                emptyList()
                            )
                        )
                    ),
                    RangeAiringDataQuery.AiringSchedule(
                        id = 3,
                        media = QueryTestDataCreator.rangeAiringDataMedia(
                            id = 3,
                            popularity = 25,
                            airingSchedule = QueryTestDataCreator.rangeAiringDataSchedule(
                                emptyList()
                            )
                        )
                    )
                )
            )
        )
        repository = AiringRepositoryImpl(
            aniListQueryExecutor,
            QueryDataToEntityMapper(),
            mediaDatabaseExecutor,
            persistentMediaDatabaseExecutor,
            clock
        )

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mediaDatabaseExecutor.updateMedia(
                mapOf(
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 2,
                            popularity = 1
                        ),
                        emptyList()
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 3,
                            popularity = 2
                        ),
                        emptyList()
                    ),
                    Pair(
                        EntityTestDataCreator.mediaItemEntity(
                            mediaId = 1,
                            popularity = 3
                        ),
                        emptyList()
                    )
                )
            )
        }
    }

    @Test(expected = ApolloException::class)
    fun loadRangeAiringData_mapperException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>()) } throws IllegalArgumentException()

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>())
        }
    }

    @Test(expected = RoomException::class)
    fun loadRangeAiringData_databaseException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { mediaDatabaseExecutor.updateMedia(any()) } throws IOException()

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test
    fun followMedia() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedules(any()) } returns flowOf(mediaFlowPair)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(emptyMap())
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val mediaItem = ModelTestDataCreator.mediaItem(id = 1)

        repository.followMedia(mediaItem)

        coVerify {
            persistentMediaDatabaseExecutor.saveMediaWithSchedules(mediaFlowPair)
        }
    }
}