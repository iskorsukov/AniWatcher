package com.iskorsukov.aniwatcher.ui.media

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.MainDispatcherRule
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption
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
class MediaViewModelTest {
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private val testData = mapOf(
        ModelTestDataCreator.baseMediaItem to
                ModelTestDataCreator.baseAiringScheduleItemList()
    )
    private val uiStateWithData = MediaScreenData(
        mediaWithNextAiringMap = mapOf(
            ModelTestDataCreator.baseMediaItem to
                    ModelTestDataCreator.baseAiringScheduleItem()
        ),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
    )

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).also {
        val timeStateFlow = MutableStateFlow(ModelTestDataCreator.TIME_IN_MINUTES)
        coEvery { it.timeInMinutesFlow } returns timeStateFlow
        coEvery { it.mediaWithSchedulesFlow } returns flowOf(testData)
    }
    private val mediaItemMapper: MediaItemMapper = mockk<MediaItemMapper>().also {
        every { it.groupAiringSchedulesByDayOfWeek(any(), any()) } returns mapOf(
            DayOfWeekLocal.SATURDAY to listOf(ModelTestDataCreator.baseAiringScheduleItemList()[2] to ModelTestDataCreator.baseMediaItem),
            DayOfWeekLocal.MONDAY to listOf(ModelTestDataCreator.baseAiringScheduleItemList()[0] to ModelTestDataCreator.baseMediaItem),
            DayOfWeekLocal.TUESDAY to listOf(ModelTestDataCreator.baseAiringScheduleItemList()[1] to ModelTestDataCreator.baseMediaItem)
        )
        every { it.groupMediaWithNextAiringSchedule(any(), any()) } returns mapOf(
            ModelTestDataCreator.baseMediaItem to ModelTestDataCreator.baseAiringScheduleItem()
        )
    }
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

    @Test
    fun mediaFlow() = runTest {
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        val result: Map<MediaItem, AiringScheduleItem?> = viewModel.uiStateWithDataFlow.value.mediaWithNextAiringMap

        assertThat(result).isNotNull()
        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem
        )
        assertThat(result.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItemList().first()
        )

        collectorJob.cancel()
    }

    @Test
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
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

        val media = viewModel.uiStateWithDataFlow.first().mediaWithNextAiringMap

        assertThat(media).isEmpty()

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

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

        collectorJob.cancel()
    }

    @Test
    fun followEvent_follow() = runTest {
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
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
                MediaUiState.DEFAULT,
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
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
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

        collectorJob.cancel()
    }

    @Test
    fun resetStateEvent() = runTest {
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        viewModel.handleInputEvent(SearchTextChangedInputEvent("search"))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        val uiState = viewModel.uiStateWithDataFlow.value

        assertThat(uiState).isEqualTo(uiStateWithData)
        verify {
            resetStateEventHandler.handleEvent(
                ResetStateTriggeredInputEvent,
                MediaUiState.DEFAULT.copy(
                    searchText = "search",
                    showReset = true
                )
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun sortingOptionEvent() = runTest {
        val baseItem = ModelTestDataCreator.baseMediaItem
        val baseItemWithBiggerMeanScore = baseItem.id(2).meanScore(2)

        coEvery { mediaItemMapper.groupMediaWithNextAiringSchedule(any(), any()) } returns mapOf(
            baseItem to ModelTestDataCreator.baseAiringScheduleItem().airingAt(1),
            baseItemWithBiggerMeanScore to ModelTestDataCreator.baseAiringScheduleItem().id(2).airingAt(2)
        )

        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.mediaWithNextAiringMap
            }
        }

        var result = viewModel.uiStateWithDataFlow.first().mediaWithNextAiringMap

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(baseItem, baseItemWithBiggerMeanScore).inOrder()

        viewModel.handleInputEvent(SortingOptionChangedInputEvent(SortingOption.SCORE))

        result = viewModel.uiStateWithDataFlow.first().mediaWithNextAiringMap

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(baseItemWithBiggerMeanScore, baseItem).inOrder()

        val uiState = viewModel.uiStateWithDataFlow.value.uiState

        assertThat(uiState.sortingOption).isEqualTo(SortingOption.SCORE)
        verify {
            sortingOptionEventHandler.handleEvent(
                SortingOptionChangedInputEvent(SortingOption.SCORE),
                MediaUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun searchTextEvent_searchTextChanged() = runTest {
        viewModel = MediaViewModel(
            airingRepository,
            mediaItemMapper,
            followEventHandler,
            searchTextEventHandler,
            formatsFilterEventHandler,
            sortingOptionEventHandler,
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
                MediaUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }
}