package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.google.common.truth.Truth.assertThat
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

    private val testData = SeasonAiringDataQuery.Data(
        SeasonAiringDataQuery.Page(
            pageInfo = SeasonAiringDataQuery.PageInfo(
                currentPage = 1,
                hasNextPage = false
            ),
            media = listOf(QueryTestDataCreator.baseSeasonAiringDataMedium())
        )
    )

    @Test
    fun seasonAiringDataQuery() = runTest {
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        coEvery {
            apolloClient.query(any<SeasonAiringDataQuery>()).execute().dataAssertNoErrors
        } returns testData

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
}