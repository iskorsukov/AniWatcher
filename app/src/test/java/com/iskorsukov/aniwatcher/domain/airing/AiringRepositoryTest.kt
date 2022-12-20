package com.iskorsukov.aniwatcher.domain.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.data.entity.combined.MediaItemAndFollowingEntity
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import com.iskorsukov.aniwatcher.domain.exception.ApolloException
import com.iskorsukov.aniwatcher.domain.exception.RoomException
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
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

    private val season = "FALL"
    private val year = 2022
    private val page = 1
    private val start = 0
    private val end = 1

    private lateinit var repository: AiringRepositoryImpl

    private val mediaFlowMap = mapOf(
        MediaItemAndFollowingEntity(
            EntityTestDataCreator.baseMediaItemEntity(),
            null
        ) to EntityTestDataCreator.baseAiringScheduleEntityList()
    )

    @Test
    fun mediaWithSchedulesFlow() = runTest {
        coEvery { mediaDatabaseExecutor.mediaDataFlow } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        val model = repository.mediaWithSchedulesFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(model.values.flatten()).containsExactlyElementsIn(ModelTestDataCreator.baseAiringScheduleItemList())

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun getMediaWithAiringSchedules() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedulesAndFollowing(any()) } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        val model = repository.getMediaWithAiringSchedules(1).first()

        assertThat(model).isNotNull()
        assertThat(model!!.first).isEqualTo(ModelTestDataCreator.baseMediaItem())
        assertThat(model.second).isEqualTo(ModelTestDataCreator.baseAiringScheduleItemList())

        coVerify {
            mediaDatabaseExecutor.getMediaWithAiringSchedulesAndFollowing(1)
        }
    }

    @Test
    fun loadSeasonAiringData() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test(expected = ApolloException::class)
    fun loadSeasonAiringData_queryException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        coEvery { aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadSeasonAiringData_mapperException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        coEvery { mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>()) } throws IllegalArgumentException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any<SeasonAiringDataQuery.Data>())
        }
    }

    @Test(expected = RoomException::class)
    fun loadSeasonAiringData_databaseException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

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
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadRangeAiringData_mapperException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        coEvery { mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>()) } throws IllegalArgumentException()

        repository.loadRangeAiringData(start, end)

        coVerify {
            aniListQueryExecutor.rangeAiringDataQuery(start, end, page)
            mapper.mapMediaWithSchedulesList(any<RangeAiringDataQuery.Data>())
        }
    }

    @Test(expected = RoomException::class)
    fun loadRangeAiringData_databaseException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

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
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        repository.followMedia(mediaItem)

        coVerify { mediaDatabaseExecutor.followMedia(mediaItem.id) }
    }

    @Test
    fun unfollowMedia() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        repository.unfollowMedia(mediaItem)

        coVerify { mediaDatabaseExecutor.unfollowMedia(mediaItem.id) }
    }

    @Test
    fun unfollowMedia_list() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        repository.unfollowMedia(listOf(mediaItem))

        coVerify { mediaDatabaseExecutor.unfollowMedia(mediaItem.id) }
    }
}