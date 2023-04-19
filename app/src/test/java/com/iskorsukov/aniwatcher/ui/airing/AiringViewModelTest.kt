package com.iskorsukov.aniwatcher.ui.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.MainDispatcherRule
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiringViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).also {
        val timeUnitFlow = MutableStateFlow(ModelTestDataCreator.TIME_IN_MINUTES)
        coEvery { it.timeInMinutesFlow } returns timeUnitFlow
        coEvery { it.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
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
    private val followEventHandler: FollowEventHandler<AiringUiState> = spyk(FollowEventHandler())
    private val formatsFilterEventHandler: FormatsFilterEventHandler<AiringUiState> = spyk(
        FormatsFilterEventHandler()
    )
    private val resetStateEventHandler: ResetStateEventHandler<AiringUiState> = spyk(
        ResetStateEventHandler()
    )

    private lateinit var viewModel: AiringViewModel

    @Before
    fun setUp() {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY
    }

    @After
    fun tearDown() {
        unmockkObject(DateTimeHelper)
    }

    @Test
    fun airingUiStateWithData() = runTest {
        viewModel = AiringViewModel(
            airingRepository,
            mediaItemMapper,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.schedulesByDayOfWeek
            }
        }

        val uiStateWithData = viewModel.uiStateWithDataFlow.value

        assertThat(uiStateWithData.timeInMinutes).isEqualTo(ModelTestDataCreator.TIME_IN_MINUTES)
        assertThat(uiStateWithData.schedulesByDayOfWeek).isNotEmpty()
        assertThat(uiStateWithData.schedulesByDayOfWeek.keys).containsExactlyElementsIn(
            listOf(
                DayOfWeekLocal.SATURDAY,
                DayOfWeekLocal.MONDAY,
                DayOfWeekLocal.TUESDAY,
            )
        ).inOrder()
        val list = ModelTestDataCreator.baseAiringScheduleToMediaPairList()
        val assertValues = listOf(
            list[2],
            list[0],
            list[1]
        )
        assertThat(uiStateWithData.schedulesByDayOfWeek.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()
        assertThat(uiStateWithData.uiState).isEqualTo(AiringUiState.DEFAULT)

        collectorJob.cancel()
    }

    @Test
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        viewModel = AiringViewModel(
            airingRepository,
            mediaItemMapper,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.schedulesByDayOfWeek
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
                AiringUiState.DEFAULT
            )
        }

        collectorJob.cancel()
    }

    @Test
    fun followEvent_follow() = runTest {
        viewModel = AiringViewModel(
            airingRepository,
            mediaItemMapper,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.schedulesByDayOfWeek
            }
        }

        viewModel.handleInputEvent(FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem))
        advanceUntilIdle()

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem),
                AiringUiState.DEFAULT,
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
        viewModel = AiringViewModel(
            airingRepository,
            mediaItemMapper,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.schedulesByDayOfWeek
            }
        }

        viewModel.handleInputEvent(
            FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true))
        )

        verify {
            followEventHandler.handleEvent(
                FollowClickedInputEvent(ModelTestDataCreator.baseMediaItem.isFollowing(true)),
                AiringUiState.DEFAULT,
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
        viewModel = AiringViewModel(
            airingRepository,
            mediaItemMapper,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
        val collectorJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiStateWithDataFlow.collect {
                it.schedulesByDayOfWeek
            }
        }

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        assertThat(viewModel.uiStateWithDataFlow.value.uiState).isEqualTo(AiringUiState.DEFAULT)
        verify {
            resetStateEventHandler.handleEvent(
                ResetStateTriggeredInputEvent,
                AiringUiState.DEFAULT.copy(
                    deselectedFormats = deselectedFormats,
                    showReset = true
                )
            )
        }

        collectorJob.cancel()
    }
}