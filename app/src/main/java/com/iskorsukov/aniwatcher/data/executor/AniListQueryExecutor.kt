package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.iskorsukov.aniwatcher.RangeAiringDataQuery
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.type.MediaSeason
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AniListQueryExecutor @Inject constructor(private val apolloClient: ApolloClient) {

    /**
     * Query for media in a selected season and year
     *
     * @param year year
     * @param season name of the anime season (WINTER, FALL, SUMMER, SPRING)
     * @param page page number for paginated data
     * @return media data in a selected season and year
     */
    suspend fun seasonAiringDataQuery(year: Int, season: String, page: Int): SeasonAiringDataQuery.Data {
        return withContext(DispatcherProvider.io()) {
            apolloClient.query(SeasonAiringDataQuery(year, MediaSeason.valueOf(season), page)).execute().dataAssertNoErrors
        }
    }

    /**
     * Query for media in a selected time range
     *
     * @param start time range start in seconds
     * @param end time range end in seconds
     * @param page page number for paginated data
     * @return media data in a selected time range
     */
    suspend fun rangeAiringDataQuery(start: Int, end: Int, page: Int): RangeAiringDataQuery.Data {
        return withContext(DispatcherProvider.io()) {
            apolloClient.query(RangeAiringDataQuery(page, start, end)).execute().dataAssertNoErrors
        }
    }
}