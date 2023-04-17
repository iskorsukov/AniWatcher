package com.iskorsukov.aniwatcher.ui.following

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FollowingViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)
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

    private fun initViewModel(testScheduler: TestCoroutineScheduler) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(followedBaseItem to airingScheduleList)
        )
        viewModel = FollowingViewModel(
            airingRepository,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )
    }

    @Test
    fun followingMediaFlow() = runTest {
        initViewModel(testScheduler)

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
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        initViewModel(testScheduler)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )

        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        val uiState = viewModel.uiStateFlow.value

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
    }

    @Test
    fun followEvent_follow() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem))
        advanceUntilIdle()

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
    }

    @Test
    fun followEvent_unfollow() = runTest {
        initViewModel(testScheduler)

        viewModel.handleInputEvent(
            FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        )
        advanceUntilIdle()

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
    }

    @Test
    fun resetStateEvent() = runTest {
        initViewModel(testScheduler)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState).isEqualTo(FollowingUiState.DEFAULT)
        verify {
            resetStateEventHandler.handleEvent(
                ResetStateTriggeredInputEvent,
                FollowingUiState.DEFAULT.copy(deselectedFormats = deselectedFormats, showReset = true)
            )
        }
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

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)
        viewModel = FollowingViewModel(
            airingRepository,
            searchTextEventHandler,
            followEventHandler,
            sortingOptionEventHandler,
            formatsFilterEventHandler,
            resetStateEventHandler
        )

        viewModel.handleInputEvent(SortingOptionChangedInputEvent(SortingOption.SCORE))

        val result = viewModel.followingMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState.sortingOption).isEqualTo(SortingOption.SCORE)
        verify {
            sortingOptionEventHandler.handleEvent(
                SortingOptionChangedInputEvent(SortingOption.SCORE),
                FollowingUiState.DEFAULT
            )
        }
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        initViewModel(testScheduler)

        val searchText = "search"

        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState.searchText).isEqualTo(searchText)
        verify {
            searchTextEventHandler.handleEvent(
                SearchTextChangedInputEvent(searchText),
                FollowingUiState.DEFAULT
            )
        }
    }
}