package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.media.MediaItemCardExtended
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun FollowingScreen(viewModel: FollowingViewModel = viewModel(), timeInMinutesFlow: Flow<Long>) {
    val followingMediaFlow by viewModel.followingMediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        followingMediaFlow.entries.forEach {
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
                    onFollowClicked = { }
                )
            }
        }
    }
}