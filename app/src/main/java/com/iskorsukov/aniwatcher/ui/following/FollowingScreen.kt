package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun FollowingScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: FollowingViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>,
    onMediaClicked: ((Int) -> Unit)? = null
) {
    val followingMediaMap by viewModel.followingMediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    LaunchedEffect(Unit) {
        mainActivityViewModel.searchTextState.collectLatest {
            viewModel.onSearchTextChanged(it)
        }
    }

    if (followingMediaMap.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_add_circle_outline_24_gray
                    ),
                    contentDescription = null,
                    tint = CardTextColorLight,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.following_data_empty_label
                    ),
                    color = CardTextColorLight,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(
                        id = R.string.following_data_empty_sub_label
                    ),
                    color = CardTextColorLight,
                    fontSize = 12.sp
                )
            }
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        followingMediaMap.entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = timeInMinutes,
                    onFollowClicked = viewModel::onFollowClicked,
                    onMediaClicked = onMediaClicked
                )
            }
        }
    }
}

@Composable
@Preview
fun FollowingScreenEmptyPlaceholderPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_baseline_add_circle_outline_24_gray
                ),
                contentDescription = null,
                tint = CardTextColorLight,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = stringResource(
                    id = R.string.following_data_empty_label
                ),
                color = CardTextColorLight,
                fontSize = 16.sp
            )
            Text(
                text = stringResource(
                    id = R.string.following_data_empty_sub_label
                ),
                color = CardTextColorLight,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
@Preview
fun FollowingScreenPreview() {
    val timeInMinutes = 27785711L

    LazyColumn {
        MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        ).filterKeys { it.isFollowing }.entries.forEach {
            item {
                MediaItemCardExtended(
                    mediaItem = it.key,
                    airingScheduleItem = it.value,
                    timeInMinutes = timeInMinutes,
                    onFollowClicked = { },
                    onMediaClicked = { }
                )
            }
        }
    }
}