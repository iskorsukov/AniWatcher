package com.iskorsukov.aniwatcher.ui.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModelDelegate
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiringViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val followableViewModelDelegate: FollowableViewModelDelegate = mockk(relaxed = true)

    private lateinit var viewModel: AiringViewModel

    @Test
    fun airingSchedulesByDayOfWeekFlow() = runTest {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        val result: Map<DayOfWeekLocal, List<AiringScheduleItem>> =
            viewModel.airingSchedulesByDayOfWeekFlow.first()

        assertThat(result).isNotNull()
        assertThat(result.keys).containsExactlyElementsIn(listOf(
            DayOfWeekLocal.SATURDAY,
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.TUESDAY,
        )).inOrder()
        val list = ModelTestDataCreator.baseAiringScheduleItemList()
        val assertValues = listOf(
            list[2],
            list[0],
            list[1]
        )
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun uiStateFlow() = runTest {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        assertThat(viewModel.uiStateFlow.value).isEqualTo(AiringUiState.DEFAULT)

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun onDeselectedFormatsChanged() = runTest {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.onDeselectedFormatsChanged(deselectedFormats)

        assertThat(viewModel.uiStateFlow.value.deselectedFormats).containsExactlyElementsIn(deselectedFormats)
        assertThat(viewModel.uiStateFlow.value.showReset).isTrue()

        viewModel.onDeselectedFormatsChanged(emptyList())
        assertThat(viewModel.uiStateFlow.value.showReset).isFalse()

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun resetState() = runTest {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.onDeselectedFormatsChanged(deselectedFormats)

        viewModel.resetState()

        assertThat(viewModel.uiStateFlow.value).isEqualTo(AiringUiState.DEFAULT)

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        val mediaItem = ModelTestDataCreator.baseMediaItem

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify {
            followableViewModelDelegate.onFollowClicked(mediaItem, any(), airingRepository, any())
        }
    }

    @Test
    fun onFollowMediaClicked_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = AiringViewModel(airingRepository, followableViewModelDelegate)

        val mediaItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify {
            followableViewModelDelegate.onFollowClicked(mediaItem, any(), airingRepository, any())
        }
    }
}