package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: MediaViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>
) {
    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val refreshing by mainActivityViewModel
        .refreshingState.collectAsStateWithLifecycle(initialValue = false)

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    val swipeRefreshState = rememberSwipeRefreshState(refreshing)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { mainActivityViewModel.loadAiringData() }
    ) {
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
}

@Composable
@Preview
fun MediaScreenPreview() {
    val timeInMinutes = 27785711L

    LazyColumn {
        MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = timeInMinutes,
                    onFollowClicked = { }
                )
            }
        }
    }
}