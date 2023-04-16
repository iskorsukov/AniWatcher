package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
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
    private val searchableViewModelDelegate: SearchableViewModelDelegate =
        SearchableViewModelDelegate()
    private val followableViewModelDelegate: FollowableViewModelDelegate =
        FollowableViewModelDelegate()

    private lateinit var viewModel: FollowingViewModel

    @Test
    fun followingMediaFlow() = runTest {
        val followedBaseItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
        val airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            followedBaseItem
        )
        assertThat(result.values).containsExactly(
            airingScheduleList.first()
        )
    }

    @Test
    fun followingMediaFlow_empty() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun sortsMediaFlow() = runTest {
        val followedBaseItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
        val followedBaseItemWithBiggerMeanScore = followedBaseItem.meanScore(2)

        val firstItem = followedBaseItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val secondItem = followedBaseItemWithBiggerMeanScore to
                listOf(
                    ModelTestDataCreator.baseAiringScheduleItem().airingAt(2)
                )
        val data = mapOf(firstItem, secondItem)

        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        var result: Map<MediaItem, AiringScheduleItem?> = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        viewModel.onSortingOptionChanged(SortingOption.SCORE)
        result = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()
    }


    @Test
    fun filtersFormat() = runTest {
        val followedBaseItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
        val followedBaseItemWithBiggerMeanScore = followedBaseItem.meanScore(2)

        val firstItem = followedBaseItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val secondItem = followedBaseItemWithBiggerMeanScore to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(2))
        val data = mapOf(firstItem, secondItem)

        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        var result = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        viewModel.onDeselectedFormatsChanged(listOf(MediaItem.LocalFormat.TV))
        result = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val mediaItem = ModelTestDataCreator.baseMediaItem

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.followMedia(mediaItem) }
    }

    @Test
    fun onFollowMediaClicked_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = FollowingViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val mediaItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.unfollowMedia(mediaItem) }
    }
}