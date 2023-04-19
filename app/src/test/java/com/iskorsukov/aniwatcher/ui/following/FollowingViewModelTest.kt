package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.MainDispatcherRule
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).also {
        val timeInMinutesFlow = MutableStateFlow(ModelTestDataCreator.TIME_IN_MINUTES)
        every { it.timeInMinutesFlow } returns timeInMinutesFlow
    }
    private val searchTextEventHandler: SearchTextEventHandler<FollowingUiState> = spyk(
        SearchTextEventHandler()
    )
    private val followEventHandler: FollowEventHandler<FollowingUiState> =
        spyk(FollowEventHandler())
    private val sortingOptionEventHandler: SortingOptionEventHandler<FollowingUiState> = spyk(
        SortingOptionEventHandler()
    )
    private val formatsFilterEventHandler: FormatsFilterEventHandler<FollowingUiState> = spyk(
        FormatsFilterEventHandler()
    )
    private val resetStateEventHandler: ResetStateEventHandler<FollowingUiState> = spyk(
        ResetStateEventHandler()
    )

    private lateinit var viewModel: FollowingViewModel

    private val followedBaseItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
    private val airingScheduleList = ModelTestDataCreator.baseAiringScheduleItemList()

    private val mediaItemMapper: MediaItemMapper = mockk<MediaItemMapper>().also {
        every { it.groupMediaWithNextAiringSchedule(any(), any()) } returns mapOf(
            followedBaseItem to airingScheduleList.first()
        )
    }

    @Test
    fun uiStateWithDataFlow() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.uiStateWithDataFlow.first().mediaWithNextAiringMap

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            followedBaseItem
        )
        assertThat(result.values).containsExactly(
            airingScheduleList.first()
        )

        collectorJob.cancel()
    }

    @Test
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )

        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

        assertThat(uiState.deselectedFormats).containsExactlyElementsIn(
            deselectedFormats
        )
        assertThat(uiState.showReset).isTrue()
        verify {
            formatsFilterEventHandler.handleEvent(
                FormatsFilterSelectionUpdatedInputEvent(deselectedFormats),
                FollowingUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun followEvent_follow() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        viewModel.handleInputEvent(FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem))

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem),
                FollowingUiState.DEFAULT,
                any(),
                airingRepository
            )
        }
        coVerify {
            airingRepository.followMedia(ModelTestDataCreator.baseMediaItem)
        }

        collectorJob.cancel()
    }

    @Test
    fun followEvent_unfollow() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        viewModel.handleInputEvent(
            FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        )

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true)),
                FollowingUiState.DEFAULT,
                any(),
                airingRepository
            )
        }
        coVerify {
            airingRepository.unfollowMedia(
                ModelTestDataCreator.baseMediaItem.isFollowing(true)
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun resetStateEvent() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

        assertThat(uiState).isEqualTo(FollowingUiState.DEFAULT)
        verify {
            resetStateEventHandler.handleEvent(
                ResetStateTriggeredInputEvent,
                FollowingUiState.DEFAULT.copy(deselectedFormats = deselectedFormats, showReset = true)
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun sortingOptionEvent() = runTest {
        val followedBaseItem = ModelTestDataCreator.baseMediaItem.isFollowing(true)
        val followedBaseItemWithBiggerMeanScore = followedBaseItem.meanScore(2)

        val firstItem = followedBaseItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val secondItem = followedBaseItemWithBiggerMeanScore to
                listOf(
                    ModelTestDataCreator.baseAiringScheduleItem().airingAt(2)
                )
        val data = mapOf(firstItem, secondItem)

        coEvery { airingRepository.followedMediaFlow } returns flowOf(data)
        coEvery { mediaItemMapper.groupMediaWithNextAiringSchedule(any(), any()) } returns mapOf(
            followedBaseItem to ModelTestDataCreator.baseAiringScheduleItem().airingAt(1),
            followedBaseItemWithBiggerMeanScore to ModelTestDataCreator.baseAiringScheduleItem().id(2).airingAt(2)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        viewModel.handleInputEvent(SortingOptionChangedInputEvent(SortingOption.SCORE))

        val result = viewModel.uiStateWithDataFlow.first().mediaWithNextAiringMap

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

        assertThat(uiState.sortingOption).isEqualTo(SortingOption.SCORE)
        verify {
            sortingOptionEventHandler.handleEvent(
                SortingOptionChangedInputEvent(SortingOption.SCORE),
                FollowingUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        coEvery { airingRepository.followedMediaFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            mediaItemMapper,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        val searchText = "search"

        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

        assertThat(uiState.searchText).isEqualTo(searchText)
        verify {
            searchTextEventHandler.handleEvent(
                SearchTextChangedInputEvent(searchText),
                FollowingUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }
}