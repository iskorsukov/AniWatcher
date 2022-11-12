package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepositoryImpl
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowingViewModelTest {
    private val airingRepositoryImpl: AiringRepositoryImpl = mockk(relaxed = true)

    private val viewModel = FollowingViewModel(airingRepositoryImpl)

    @Test
    fun followingMediaFlow() = runTest {
        coEvery { airingRepositoryImpl.mediaWithSchedulesFlow } returns flow {
            emit(
                mapOf(
                    ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                            ModelTestDataCreator.baseAiringScheduleItemList()
                )
            )
        }

        var result: Map<MediaItem, AiringScheduleItem?>? = null
        viewModel.followingMediaFlow.collectLatest { result = it }

        assertThat(result).isNotNull()
        assertThat(result!!.size).isEqualTo(1)
        assertThat(result!!.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem().isFollowing(true))
        assertThat(result!!.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItem().id(4).episode(4).airingAt(1667029460))
    }

    @Test
    fun followingMediaFlow_empty() = runTest {
        coEvery { airingRepositoryImpl.mediaWithSchedulesFlow } returns flow {
            emit(
                mapOf(
                    ModelTestDataCreator.baseMediaItem() to
                            ModelTestDataCreator.baseAiringScheduleItemList()
                )
            )
        }

        var result: Map<MediaItem, AiringScheduleItem?>? = null
        viewModel.followingMediaFlow.collectLatest { result = it }

        assertThat(result).isNotNull()
        assertThat(result!!.size).isEqualTo(0)
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepositoryImpl.followMedia(mediaItem) }
    }

    @Test
    fun onFollowMediaClicked_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val mediaItem = ModelTestDataCreator.baseMediaItem().isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepositoryImpl.unfollowMedia(mediaItem) }
    }
}