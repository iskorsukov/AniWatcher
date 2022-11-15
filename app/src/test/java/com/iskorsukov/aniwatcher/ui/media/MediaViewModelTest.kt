package com.iskorsukov.aniwatcher.ui.media

import com.google.common.truth.Truth
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private lateinit var viewModel: MediaViewModel

    @Test
    fun mediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = MediaViewModel(airingRepository)

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.mediaFlow.first()

        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem())
        Truth.assertThat(result.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItem().id(4).episode(4).airingAt(1667029460))
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(airingRepository)

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.followMedia(mediaItem) }
    }

    @Test
    fun onFollowMediaClicked_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(airingRepository)

        val mediaItem = ModelTestDataCreator.baseMediaItem().isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.unfollowMedia(mediaItem) }
    }
}