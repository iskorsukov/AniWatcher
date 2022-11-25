package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
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
class FollowingViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private lateinit var viewModel: FollowingViewModel

    @Test
    fun followingMediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = FollowingViewModel(airingRepository)

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem().isFollowing(true))
        assertThat(result.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItem().id(4).episode(4).airingAt(1667029460))
    }

    @Test
    fun finishedFollowingShowsFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        listOf(ModelTestDataCreator.baseAiringScheduleItem(true).airingAt(1))
            )
        )
        viewModel = FollowingViewModel(airingRepository)

        val result = viewModel.finishedFollowingShowsFlow.first()

        assertThat(result.size).isEqualTo(1)
        assertThat(result).containsExactly(
            ModelTestDataCreator.baseMediaItem().isFollowing(true)
        )
    }

    @Test
    fun followingMediaFlow_empty() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = FollowingViewModel(airingRepository)

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun unfollowFinishedShows() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                        listOf(ModelTestDataCreator.baseAiringScheduleItem(true).airingAt(1))
            )
        )
        viewModel = FollowingViewModel(airingRepository)

        viewModel.unfollowFinishedShows()
        advanceUntilIdle()

        coVerify {
            airingRepository.unfollowMedia(
                listOf(ModelTestDataCreator.baseMediaItem().isFollowing(true))
            )
        }
    }
}