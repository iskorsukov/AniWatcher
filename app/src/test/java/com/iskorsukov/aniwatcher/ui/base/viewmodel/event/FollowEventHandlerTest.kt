package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.airing.AiringUiState
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowEventHandlerTest {

    private val followEventHandler: FollowEventHandler<AiringUiState> = FollowEventHandler()

    private val airingRepository: AiringRepository = mockk(relaxed = true)

    @Test
    fun handleEvent_follow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        followEventHandler.handleEvent(
            FollowClickedInputEvent(
                ModelTestDataCreator.baseMediaItem
            ),
            AiringUiState.DEFAULT,
            CoroutineScope(Dispatchers.Main),
            airingRepository
        )
        advanceUntilIdle()

        coVerify {
            airingRepository.followMedia(ModelTestDataCreator.baseMediaItem)
        }
    }

    @Test
    fun handleEvent_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val uiState = followEventHandler.handleEvent(
            FollowClickedInputEvent(
                ModelTestDataCreator.baseMediaItem.isFollowing(true)
            ),
            AiringUiState.DEFAULT,
            CoroutineScope(Dispatchers.Main),
            airingRepository
        )
        advanceUntilIdle()

        assertThat(uiState).isEqualTo(AiringUiState.DEFAULT)
        coVerify {
            airingRepository.unfollowMedia(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        }
    }
}