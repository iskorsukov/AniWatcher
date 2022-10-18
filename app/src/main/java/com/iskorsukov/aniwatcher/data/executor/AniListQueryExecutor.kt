package com.iskorsukov.aniwatcher.data.executor

import com.apollographql.apollo3.ApolloClient
import com.iskorsukov.aniwatcher.WeekAiringDataQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AniListQueryExecutor(private val apolloClient: ApolloClient) {

    suspend fun weekAiringDataQuery(weekStart: Int, weekEnd: Int): WeekAiringDataQuery.Data {
        return withContext(Dispatchers.IO) {
            apolloClient.query(WeekAiringDataQuery(weekStart, weekEnd)).execute().dataAssertNoErrors
        }
    }
}