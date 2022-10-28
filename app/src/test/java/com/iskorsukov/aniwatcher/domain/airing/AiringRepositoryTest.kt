package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.executor.MediaDatabaseExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToEntityMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class AiringRepositoryTest {
    private val aniListQueryExecutor: AniListQueryExecutor = mockk(relaxed = true)
    private val mapper: QueryDataToEntityMapper = mockk(relaxed = true)
    private val mediaDatabaseExecutor: MediaDatabaseExecutor = mockk(relaxed = true)

    private val season = "FALL"
    private val year = 2022
    private val page = 1

    private val repository = AiringRepository(aniListQueryExecutor, mapper, mediaDatabaseExecutor)

    @Test
    fun loadWeekAiringData() = runTest {
        repository.loadSeasonAiringData(year, season, page)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }

    @Test(expected = IOException::class)
    fun loadWeekAiringData_queryException() = runTest {
        coEvery { aniListQueryExecutor.seasonAiringDataQuery(any(), any(), any()) } throws IOException()

        repository.loadSeasonAiringData(year, season, page)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun loadWeekAiringData_mapperException() = runTest {
        coEvery { mapper.mapMediaWithSchedulesList(any()) } throws IllegalArgumentException()

        repository.loadSeasonAiringData(year, season, page)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
        }
    }

    @Test(expected = IOException::class)
    fun loadWeekAiringData_databaseException() = runTest {
        coEvery { mediaDatabaseExecutor.updateMedia(any()) } throws IOException()

        repository.loadSeasonAiringData(year, season, page)

        coVerify {
            aniListQueryExecutor.seasonAiringDataQuery(year, season, page)
            mapper.mapMediaWithSchedulesList(any())
            mediaDatabaseExecutor.updateMedia(any())
        }
    }
}