package com.iskorsukov.aniwatcher.ui.media

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
class MediaViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val followEventHandler: FollowEventHandler<MediaUiState> = spyk(FollowEventHandler())
    private val searchTextEventHandler: SearchTextEventHandler<MediaUiState> = spyk(
        SearchTextEventHandler()
    )
    private val formatsFilterEventHandler: FormatsFilterEventHandler<MediaUiState> = spyk(
        FormatsFilterEventHandler()
    )
    private val sortingOptionEventHandler: SortingOptionEventHandler<MediaUiState> = spyk(
        SortingOptionEventHandler()
    )
    private val resetStateEventHandler: ResetStateEventHandler<MediaUiState> = spyk(
        ResetStateEventHandler()
    )

    private lateinit var viewModel: MediaViewModel

    private fun initViewModel(testScheduler: TestCoroutineScheduler) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = MediaViewModel(
            airingRepository,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
            resetStateEventHandler
        )
    }

    @Test
    fun mediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

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
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )

        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        val media = viewModel.mediaFlow.first()

        assertThat(media).isEmpty()

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState.deselectedFormats).containsExactlyElementsIn(
            deselectedFormats
        )
        assertThat(uiState.showReset).isTrue()
        verify {
            formatsFilterEventHandler.handleEvent(
                FormatsFilterSelectionUpdatedInputEvent(deselectedFormats),
                MediaUiState.DEFAULT
            )
        }
    }

    @Test
    fun followEvent_follow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

        viewModel.handleInputEvent(FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem))
        advanceUntilIdle()

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem),
                MediaUiState.DEFAULT,
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
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

        viewModel.handleInputEvent(
            FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        )
        advanceUntilIdle()

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true)),
                MediaUiState.DEFAULT,
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
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState).isEqualTo(MediaUiState.DEFAULT)
        verify {
            resetStateEventHandler.handleEvent(
                ResetStateTriggeredInputEvent,
                MediaUiState.DEFAULT.copy(deselectedFormats = deselectedFormats, showReset = true)
            )
        }
    }

    @Test
    fun sortingOptionEvent() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )

        val baseItem = ModelTestDataCreator.baseMediaItem
        val baseItemWithBiggerMeanScore = baseItem.meanScore(2)

        val firstItem = baseItem to
                listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
        val secondItem = baseItemWithBiggerMeanScore to
                listOf(
                    ModelTestDataCreator.baseAiringScheduleItem().airingAt(2)
                )
        val data = mapOf(firstItem, secondItem)

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(data)

        initViewModel(testScheduler)

        viewModel.handleInputEvent(SortingOptionChangedInputEvent(SortingOption.SCORE))

        val result = viewModel.mediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState.sortingOption).isEqualTo(SortingOption.SCORE)
        verify {
            sortingOptionEventHandler.handleEvent(
                SortingOptionChangedInputEvent(SortingOption.SCORE),
                MediaUiState.DEFAULT
            )
        }
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        initViewModel(testScheduler)

        val searchText = "search"
        viewModel.handleInputEvent(SearchTextChangedInputEvent(searchText))

        val uiState = viewModel.uiStateFlow.value

        assertThat(uiState.searchText).isEqualTo(searchText)
        verify {
            searchTextEventHandler.handleEvent(
                SearchTextChangedInputEvent(searchText),
                MediaUiState.DEFAULT
            )
        }
    }
}