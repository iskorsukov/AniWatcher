package com.iskorsukov.aniwatcher.ui.media

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
class MediaViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val searchableViewModelDelegate: SearchableViewModelDelegate = SearchableViewModelDelegate()
    private val followableViewModelDelegate: FollowableViewModelDelegate = FollowableViewModelDelegate()

    private lateinit var viewModel: MediaViewModel

    @Test
    fun mediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = MediaViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.mediaFlow.first()

        assertThat(result).isNotNull()
        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem
        )
        assertThat(result.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItemList().first()
        )
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(
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
        viewModel = MediaViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        val mediaItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.unfollowMedia(mediaItem) }
    }

    @Test
    fun sortsMediaFlow() = runTest {
        val firstItem = ModelTestDataCreator.baseMediaItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val baseMediaItemWithBiggerMeanScore = ModelTestDataCreator.baseMediaItem.meanScore(2)
        val secondItem = baseMediaItemWithBiggerMeanScore to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(2))
        val data = mapOf(firstItem, secondItem)
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)

        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        var result = viewModel.mediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        viewModel.onSortingOptionChanged(SortingOption.SCORE)
        result = viewModel.mediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()
    }

    @Test
    fun filtersFormat() = runTest {
        val firstItem = ModelTestDataCreator.baseMediaItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val baseMediaItemWithBiggerMeanScore = ModelTestDataCreator.baseMediaItem.meanScore(2)
        val secondItem = baseMediaItemWithBiggerMeanScore to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(2))
        val data = mapOf(firstItem, secondItem)
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)

        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(
            airingRepository,
            searchableViewModelDelegate,
            followableViewModelDelegate
        )

        var result = viewModel.mediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        viewModel.onDeselectedFormatsChanged(listOf(MediaItem.LocalFormat.TV))
        result = viewModel.mediaFlow.first()

        assertThat(result.size).isEqualTo(0)
    }
}