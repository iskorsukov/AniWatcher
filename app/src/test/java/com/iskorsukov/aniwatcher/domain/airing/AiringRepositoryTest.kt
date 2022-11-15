package com.iskorsukov.aniwatcher.domain.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
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

    private val repository = AiringRepositoryImpl(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

    @Test
    fun getMediaWithAiringSchedules() = runTest {
        coEvery { mediaDatabaseExecutor.getMediaWithAiringSchedulesAndFollowing(any()) } returns flowOf(
            EntityTestDataCreator.baseMediaItemWithAiringSchedulesAndFollowingEntity()
        )

        val model = repository.getMediaWithAiringSchedules(1).first()

        assertThat(model.first).isEqualTo(ModelTestDataCreator.baseMediaItem())
        assertThat(model.second).isEqualTo(ModelTestDataCreator.baseAiringScheduleItemList())

        coVerify {
            mediaDatabaseExecutor.getMediaWithAiringSchedulesAndFollowing(1)
        }
    }

    @Test
    fun loadSeasonAiringData() = runTest {
        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test(expected = IOException::class)
    fun loadSeasonAiringData_queryException() = runTest {
        coEvery { aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadSeasonAiringData_mapperException() = runTest {
        coEvery { mapper.mapMediaWithSchedulesList(any()) } throws IllegalArgumentException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
        }
    }

    @Test(expected = IOException::class)
    fun loadSeasonAiringData_databaseException() = runTest {
        coEvery { mediaDatabaseExecutor.updateMedia(any()) } throws IOException()

        repository.loadSeasonAiringData(year, season)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test
    fun followMedia() = runTest {
        val mediaItem = ModelTestDataCreator.baseMediaItem()

        repository.followMedia(mediaItem)

        coVerify { mediaDatabaseExecutor.followMedia(mediaItem.id) }
    }

    @Test
    fun unfollowMedia() = runTest {
        val mediaItem = ModelTestDataCreator.baseMediaItem()

        repository.unfollowMedia(mediaItem)

        coVerify { mediaDatabaseExecutor.unfollowMedia(mediaItem.id) }
    }

    @Test
    fun clearMediaSchedules() = runTest {
        repository.clearAiredSchedules()

        coVerify { mediaDatabaseExecutor.clearAiredSchedules() }
    }
}