package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.fab.ScrollToTopFab
import com.iskorsukov.aniwatcher.ui.base.header.FilterFormatHeaderChip
import com.iskorsukov.aniwatcher.ui.base.header.HeaderFlowRow
import com.iskorsukov.aniwatcher.ui.base.format.FilterFormatDialog
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardCollapsed
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AiringScreen(
    viewModel: AiringViewModel,
    mediaItemMapper: MediaItemMapper,
    isRefreshing: Boolean,
    settingsState: SettingsState,
    onMediaClicked: (MediaItem) -> Unit,
    onRefresh: () -> Unit
) {
    val airingScreenData by viewModel.dataFlow
        .collectAsStateWithLifecycle()
    val airingScreenState = rememberAiringScreenState(
        uiStateWithData = airingScreenData,
        mediaItemMapper = mediaItemMapper
    )

    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        AiringScreenContent(
            lazyListState = lazyListState,
            airingScreenState = airingScreenState,
            onFollowClicked = {
                viewModel.onFollowMedia(it)
            },
            onMediaClicked = onMediaClicked,
            preferredNamingScheme = settingsState.preferredNamingScheme,
        )
        ScrollToTopFab(
            lazyListState = lazyListState,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = LocalColors.current.onPrimary,
            contentColor = LocalColors.current.primary
        )
    }

    if (airingScreenState.filterFormatDialogState.shouldShowFilterFormatDialog) {
        FilterFormatDialog(
            filterFormatDialogState = airingScreenState.filterFormatDialogState
        )
    }
}

@Composable
private fun AiringScreenContent(
    lazyListState: LazyListState,
    airingScreenState: AiringScreenState,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: (MediaItem) -> Unit,
    preferredNamingScheme: NamingScheme
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            HeaderFlowRow(
                showReset = airingScreenState.shouldShowResetButton,
                onResetClicked = { airingScreenState.reset() }
            ) {
                FilterFormatHeaderChip(
                    deselectedFormats = airingScreenState.filterFormatDialogState.deselectedFormats
                ) {
                    airingScreenState.filterFormatDialogState.show()
                }
            }
        }
        airingScreenState.mediaWithNextAiringMap.entries.map {
            item {
                Text(
                    text = it.key.name,
                    style = LocalTextStyles.current.category,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        .fillMaxWidth()
                )
            }
            it.value.map {
                item {
                    MediaItemCardCollapsed(
                        airingScheduleItem = it.first,
                        mediaItem = it.second,
                        timeInMinutes = airingScreenState.uiStateWithData.timeInMinutes,
                        onFollowClicked = onFollowClicked,
                        onMediaClicked = onMediaClicked,
                        preferredNamingScheme = preferredNamingScheme
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun AiringScreenPreview() {
    val lazyListState = rememberLazyListState()
    val airingScreenState = rememberAiringScreenState(
        uiStateWithData = AiringScreenData(
            mediaWithSchedulesMap = mapOf(
                ModelTestDataCreator.previewData()
            ),
            timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
        ),
        mediaItemMapper = MediaItemMapper()
    )
    Box(modifier = Modifier.fillMaxSize()) {
        AiringScreenContent(
            lazyListState = lazyListState,
            airingScreenState = airingScreenState,
            onFollowClicked = { },
            onMediaClicked = { },
            preferredNamingScheme = NamingScheme.ENGLISH
        )
    }
}