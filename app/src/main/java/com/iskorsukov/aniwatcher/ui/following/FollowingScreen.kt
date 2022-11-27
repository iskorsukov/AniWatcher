package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun FollowingScreen(
    viewModel: FollowingViewModel,
    uiState: MainActivityUiState,
    settingsState: SettingsState,
    timeInMinutes: Long,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit
) {
    val followingMediaMap by viewModel.followingMediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val finishedShowsList by viewModel.finishedFollowingShowsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val listState = rememberLazyListState()
    LaunchedEffect(uiState.searchText, uiState.sortingOption) {
        viewModel.onSearchTextChanged(uiState.searchText)
        viewModel.onSortingOptionChanged(uiState.sortingOption)
        listState.scrollToItem(0)
    }

    var shouldShowFinishedShowsDialog by remember(finishedShowsList.size) {
        mutableStateOf(finishedShowsList.isNotEmpty())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (shouldShowFinishedShowsDialog) {
            FinishedFollowingSurface(
                onActionClicked = { viewModel.unfollowFinishedShows() },
                onDismissRequest = { shouldShowFinishedShowsDialog = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
        FollowingScreenContent(
            followingMediaMap = followingMediaMap,
            timeInMinutes = timeInMinutes,
            onFollowClicked = viewModel::onFollowClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onMediaClicked = onMediaClicked,
            onGenreChipClicked = onGenreChipClicked,
            listState = listState
        )
    }
}

@Composable
fun FollowingScreenContent(
    followingMediaMap: Map<MediaItem, AiringScheduleItem?>,
    timeInMinutes: Long,
    onFollowClicked: ((MediaItem) -> Unit),
    preferredNamingScheme: NamingScheme,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit,
    listState: LazyListState,
) {
    if (followingMediaMap.isEmpty()) {
        EmptyDataPlaceholder(
            iconResId = R.drawable.ic_baseline_add_circle_outline_24_gray,
            labelResId = R.string.following_data_empty_label,
            subLabelResId = R.string.following_data_empty_sub_label,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            followingMediaMap.entries.forEach {
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

@Composable
@Preview
private fun FollowingScreenEmptyPreview() {
    FollowingScreenContent(
        followingMediaMap = emptyMap(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState()
    )
}

@Composable
@Preview
private fun FollowingScreenPreview() {
    FollowingScreenContent(
        followingMediaMap = MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).filterKeys { it.isFollowing },
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState()
    )
}