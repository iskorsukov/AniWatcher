package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.iskorsukov.aniwatcher.SeasonAiringDataQuery
import com.iskorsukov.aniwatcher.domain.util.DispatcherProvider
import com.iskorsukov.aniwatcher.type.MediaSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AniListQueryExecutor @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun seasonAiringDataQuery(year: Int, season: String, page: Int): SeasonAiringDataQuery.Data {
        return withContext(DispatcherProvider.io()) {
            apolloClient.query(SeasonAiringDataQuery(year, MediaSeason.valueOf(season), page)).execute().dataAssertNoErrors
        }
    }
}