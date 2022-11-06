package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowingViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private val viewModel = FollowingViewModel(airingRepository)

    @Test
    fun followingMediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flow {
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
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flow {
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
}