package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(viewModel: MediaViewModel = viewModel(), timeInMinutesFlow: Flow<Long>) {
    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        mediaFlow.entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = timeInMinutes,
                    onFollowClicked = viewModel::onFollowClicked
                )
            }
        }
    }
}