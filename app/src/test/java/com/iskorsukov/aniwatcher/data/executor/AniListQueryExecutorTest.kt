package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.QueryTestDataCreator
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AniListQueryExecutorTest {

    private val apolloClient: ApolloClient = mockk(relaxed = true)

    private val aniListQueryExecutor = AniListQueryExecutor(apolloClient)

    @Test
    fun seasonAiringDataQuery() = runTest {
        mockkObject(DispatcherProvider)
        every { DispatcherProvider.io() } returns StandardTestDispatcher(testScheduler)

        coEvery { apolloClient.query(any<SeasonAiringDataQuery>()).execute().dataAssertNoErrors } returns SeasonAiringDataQuery.Data(
            SeasonAiringDataQuery.Page(
                pageInfo = SeasonAiringDataQuery.PageInfo(
                    currentPage = 1,
                    hasNextPage = true
                ),
                media = listOf(QueryTestDataCreator.baseSeasonAiringDataMedium())
            )
        )

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