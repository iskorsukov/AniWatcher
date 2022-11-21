package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: MediaViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>,
    onMediaClicked: (MediaItem) -> Unit
) {
    val uiState by mainActivityViewModel
        .uiState.collectAsStateWithLifecycle()

    val settingsState by mainActivityViewModel
        .settingsState.collectAsStateWithLifecycle()

    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.searchText, uiState.sortingOption) {
        viewModel.onSearchTextChanged(uiState.searchText)
        viewModel.onSortingOptionChanged(uiState.sortingOption)
        listState.scrollToItem(0)
    }

    MediaScreenContent(
        mediaItemWithNextAiringMap = mediaFlow,
        swipeRefreshState = swipeRefreshState,
        listState = listState,
        timeInMinutes = timeInMinutes,
        preferredNamingScheme = settingsState.preferredNamingScheme,
        onRefresh = { mainActivityViewModel.loadAiringData() },
        onFollowClicked = viewModel::onFollowClicked,
        onMediaClicked = onMediaClicked
    )
}

@Composable
private fun MediaScreenContent(
    mediaItemWithNextAiringMap: Map<MediaItem, AiringScheduleItem?>,
    swipeRefreshState: SwipeRefreshState,
    listState: LazyListState,
    timeInMinutes: Long,
    preferredNamingScheme: NamingScheme,
    onRefresh: () -> Unit,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {
        Column {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                mediaItemWithNextAiringMap.entries.forEach {
                    item {
                        MediaItemCardExtended(
                            mediaItem = it.key,
                            airingScheduleItem = it.value,
                            timeInMinutes = timeInMinutes,
                            onFollowClicked = onFollowClicked,
                            onMediaClicked = onMediaClicked,
                            preferredNamingScheme = preferredNamingScheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MediaScreenPreview() {
    val timeInMinutes = 27785710L

    MediaScreenContent(
        mediaItemWithNextAiringMap = MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ),
        swipeRefreshState = rememberSwipeRefreshState(false),
        listState = rememberLazyListState(),
        timeInMinutes = timeInMinutes,
        preferredNamingScheme = NamingScheme.ENGLISH,
        onRefresh = { },
        onFollowClicked = { },
        onMediaClicked = { }
    )
}