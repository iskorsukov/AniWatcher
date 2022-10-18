package com.iskorsukov.aniwatcher.domain.airing

import com.iskorsukov.aniwatcher.data.executor.AniListQueryExecutor
import com.iskorsukov.aniwatcher.data.mapper.QueryDataToDomainMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class WeekAiringRepositoryTest {
    private val executor: AniListQueryExecutor = mockk(relaxed = true)
    private val mapper: QueryDataToDomainMapper = mockk(relaxed = true)

    private val repository = WeekAiringRepository(executor, mapper)

    @Test
    fun loadWeekAiringData() = runTest {
        val weekStart = 1
        val weekEnd = 2

        repository.loadWeekAiringData(weekStart, weekEnd)

        coVerify {
            executor.weekAiringDataQuery(weekStart, weekEnd)
            mapper.map(any())
        }
    }

    @Test(expected = IOException::class)
    fun loadWeekAiringData_Exception() = runTest {
        val weekStart = 1
        val weekEnd = 2
        coEvery { executor.weekAiringDataQuery(any(), any()) } throws IOException()

        repository.loadWeekAiringData(weekStart, weekEnd)
    }
}