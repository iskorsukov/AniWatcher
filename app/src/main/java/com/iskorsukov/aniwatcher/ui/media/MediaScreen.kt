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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    viewModel: MediaViewModel,
    uiState: MainActivityUiState,
    settingsState: SettingsState,
    timeInMinutes: Long,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit,
    onGenreChipClicked: (String) -> Unit
) {
    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.searchText, uiState.sortingOption) {
        viewModel.onSearchTextChanged(uiState.searchText)
        viewModel.onSortingOptionChanged(uiState.sortingOption)
        listState.scrollToItem(0)
    }

    MediaScreenContent(
        mediaItemWithNextAiringMap = MediaItemMapper.filterExtraFollowedMedia(
            mediaFlow, settingsState, uiState.seasonYear
        ),
        swipeRefreshState = swipeRefreshState,
        listState = listState,
        timeInMinutes = timeInMinutes,
        preferredNamingScheme = settingsState.preferredNamingScheme,
        onRefresh = onRefresh,
        onFollowClicked = viewModel::onFollowClicked,
        onGenreChipClicked = onGenreChipClicked,
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
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit,
) {
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        indicator = { state, triggerDistance ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = triggerDistance,
                backgroundColor = LocalColors.current.onPrimary,
                contentColor = LocalColors.current.primary
            )
        }
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
                            onGenreChipClicked = onGenreChipClicked,
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
    MediaScreenContent(
        mediaItemWithNextAiringMap = MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ),
        swipeRefreshState = rememberSwipeRefreshState(false),
        listState = rememberLazyListState(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        preferredNamingScheme = NamingScheme.ENGLISH,
        onRefresh = { },
        onFollowClicked = { },
        onMediaClicked = { },
        onGenreChipClicked = { }
    )
}