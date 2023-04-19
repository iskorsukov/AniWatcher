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
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class AiringRepositoryTest {
    private val aniListQueryExecutor: AniListQueryExecutor = mockk(relaxed = true)
    private val mapper: QueryDataToEntityMapper = mockk(relaxed = true)
    private val mediaDatabaseExecutor: MediaDatabaseExecutor = mockk(relaxed = true)
    private val persistentMediaDatabaseExecutor: PersistentMediaDatabaseExecutor = mockk(relaxed = true)
    private val clock: LocalClockSystem = mockk<LocalClockSystem>(relaxed = true).also {
        coEvery { it.currentTimeMillis() } returns TimeUnit.MINUTES.toMillis(ModelTestDataCreator.TIME_IN_MINUTES)
    }

    private val season = "FALL"
    private val year = 2022
    private val page = 1
    private val start = 0
    private val end = 1

    private lateinit var repository: AiringRepositoryImpl

    private val mediaFlowPair = EntityTestDataCreator.baseMediaItemEntity() to EntityTestDataCreator.baseAiringScheduleEntityList()
    private val mediaFlowMap = mapOf(mediaFlowPair)

    @Test
    fun mediaWithSchedulesFlow() = runTest {
        coEvery { mediaDatabaseExecutor.mediaDataFlow } returns flowOf(mediaFlowMap)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(emptyMap())
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.mediaWithSchedulesFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(ModelTestDataCreator.baseMediaItem)
        assertThat(model.values.flatten()).containsExactlyElementsIn(ModelTestDataCreator.baseAiringScheduleItemList())

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun mediaWithSchedulesFlow_mediaFollowed() = runTest {
        coEvery { mediaDatabaseExecutor.mediaDataFlow } returns flowOf(mediaFlowMap)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(mediaFlowMap)
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.mediaWithSchedulesFlow.first()

        assertThat(model).isNotNull()
        assertThat(model.keys).containsExactly(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        assertThat(model.values.flatten()).containsExactlyElementsIn(ModelTestDataCreator.baseAiringScheduleItemList())

        coVerify { mediaDatabaseExecutor.mediaDataFlow }
    }

    @Test
    fun getMediaWithAiringSchedules() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedules(any()) } returns flowOf(mediaFlowPair)
        coEvery { persistentMediaDatabaseExecutor.followedMediaFlow } returns flowOf(emptyMap())
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val model = repository.getMediaWithAiringSchedules(1).first()

        assertThat(model).isNotNull()
        assertThat(model!!.first).isEqualTo(ModelTestDataCreator.baseMediaItem)
        assertThat(model.second).isEqualTo(ModelTestDataCreator.baseAiringScheduleItemList())

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

    @Test(expected = ApolloException::class)
    fun loadSeasonAiringData_queryException() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        coEvery { aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        }
    }

    @Test(expected = IllegalArgumentException::class)
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

    @Test(expected = IllegalArgumentException::class)
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

        val mediaItem = ModelTestDataCreator.baseMediaItem

        repository.followMedia(mediaItem)

        coVerify {
            persistentMediaDatabaseExecutor.saveMediaWithSchedules(mediaFlowPair)
        }
    }

    @Test
    fun unfollowMedia() = runTest {
        repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor, persistentMediaDatabaseExecutor, clock)

        val mediaItem = ModelTestDataCreator.baseMediaItem

        repository.unfollowMedia(mediaItem)

        coVerify { persistentMediaDatabaseExecutor.deleteMedia(mediaItem.id) }
    }
}