package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.placeholder.EmptyDataPlaceholder
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

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

    val followingUiState by viewModel.uiStateFlow
        .collectAsStateWithLifecycle()

    val finishedShowsList by viewModel.finishedFollowingShowsFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val listState = rememberLazyListState()
    LaunchedEffect(uiState.searchText, followingUiState.sortingOption) {
        viewModel.onSearchTextChanged(uiState.searchText)
        listState.scrollToItem(0)
    }

    /* TODO: Re-enable after finished shows detection is finished
    var shouldShowFinishedShowsDialog by remember(finishedShowsList.size) {
        mutableStateOf(finishedShowsList.isNotEmpty())
    }
     */
    var shouldShowFinishedShowsDialog = false

    var shouldShowSortingOptionsDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var shouldShowFilterFormatDialog by rememberSaveable {
        mutableStateOf(false)
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
            searchTextIsEmpty = uiState.searchText.trim().length < 4,
            timeInMinutes = timeInMinutes,
            onFollowClicked = viewModel::onFollowClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme,
            onMediaClicked = onMediaClicked,
            onGenreChipClicked = onGenreChipClicked,
            listState = listState,
            selectedSortingOption = followingUiState.sortingOption,
            onSelectSortingOptionClicked = { shouldShowSortingOptionsDialog = true },
            deselectedFormats = followingUiState.deselectedFormats,
            onFilterFormatClicked = { shouldShowFilterFormatDialog = true }
        )
    }

    if (shouldShowSortingOptionsDialog) {
        SelectSortingOptionDialog(
            onSortingOptionSelected = viewModel::onSortingOptionChanged,
            onDismissRequest = { shouldShowSortingOptionsDialog = false },
            selectedOption = followingUiState.sortingOption
        )
    }
    if (shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            followingUiState.deselectedFormats
        ) { formats ->
            shouldShowFilterFormatDialog = false
            viewModel.onDeselectedFormatsChanged(formats)
        }
    }
}

@Composable
fun FollowingScreenContent(
    followingMediaMap: Map<MediaItem, AiringScheduleItem?>,
    searchTextIsEmpty: Boolean,
    timeInMinutes: Long,
    onFollowClicked: ((MediaItem) -> Unit),
    preferredNamingScheme: NamingScheme,
    onMediaClicked: (MediaItem) -> Unit,
    onGenreChipClicked: (String) -> Unit,
    listState: LazyListState,
    selectedSortingOption: SortingOption,
    onSelectSortingOptionClicked: () -> Unit,
    deselectedFormats: List<MediaItem.LocalFormat>,
    onFilterFormatClicked: () -> Unit
) {
    if (followingMediaMap.isEmpty() && searchTextIsEmpty) {
        EmptyDataPlaceholder(
            iconResId = R.drawable.ic_baseline_add_circle_outline_24_gray,
            labelResId = R.string.following_data_empty_label,
            subLabelResId = R.string.following_data_empty_sub_label,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            item {
                HeaderFlowRow(
                    selectedSortingOption = selectedSortingOption,
                    onSelectSortingOptionClicked = onSelectSortingOptionClicked,
                    deselectedFormats = deselectedFormats,
                    onFilterFormatsClicked = onFilterFormatClicked
                )
            }
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
        searchTextIsEmpty = true,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState(),
        selectedSortingOption = SortingOption.AIRING_AT,
        onSelectSortingOptionClicked = { },
        deselectedFormats = emptyList(),
        onFilterFormatClicked = { }
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
        searchTextIsEmpty = true,
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES,
        onFollowClicked = {},
        preferredNamingScheme = NamingScheme.ENGLISH,
        onMediaClicked = {},
        onGenreChipClicked = {},
        listState = rememberLazyListState(),
        selectedSortingOption = SortingOption.AIRING_AT,
        onSelectSortingOptionClicked = { },
        deselectedFormats = emptyList(),
        onFilterFormatClicked = { }
    )
}