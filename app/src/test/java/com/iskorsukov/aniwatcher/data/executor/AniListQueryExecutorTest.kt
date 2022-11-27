package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AniListQueryExecutorTest {

    private val apolloClient: ApolloClient = mockk(relaxed = true)

    private val aniListQueryExecutor = AniListQueryExecutor(apolloClient)

    private val testDataSeason = SeasonAiringDataQuery.Data(
        SeasonAiringDataQuery.Page(
            pageInfo = SeasonAiringDataQuery.PageInfo(
                currentPage = 1,
                hasNextPage = false
            ),
            media = listOf(QueryTestDataCreator.baseSeasonAiringDataMedium())
        )
    )

    private val testDataRange = RangeAiringDataQuery.Data(
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

    @Test
    fun seasonAiringDataQuery() = runTest {
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        coEvery {
            apolloClient.query(any<SeasonAiringDataQuery>()).execute().dataAssertNoErrors
        } returns testDataSeason

        val season = "FALL"
        val year = 2022
        val page = 1

        val data = aniListQueryExecutor.seasonAiringDataQuery(year, season, page)

        assertThat(data.Page!!.media).containsExactly(QueryTestDataCreator.baseSeasonAiringDataMedium())

        coVerify {
            apolloClient.query(any<SeasonAiringDataQuery>()).execute().dataAssertNoErrors
        }

        unmockkObject(DispatcherProvider)
    }

    @Test
    fun rangeAiringDataQuery() = runTest {
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        coEvery {
            apolloClient.query(any<RangeAiringDataQuery>()).execute().dataAssertNoErrors
        } returns testDataRange

        val start = 0
        val end = 1
        val page = 1

        val data = aniListQueryExecutor.rangeAiringDataQuery(start, end, page)

        assertThat(data.Page!!.airingSchedules).containsExactly(
            RangeAiringDataQuery.AiringSchedule(
                id = 1,
                media = QueryTestDataCreator.baseRangeAiringDataMedium()
            )
        )

        coVerify {
            apolloClient.query(any<RangeAiringDataQuery>()).execute().dataAssertNoErrors
        }

        unmockkObject(DispatcherProvider)
    }
}