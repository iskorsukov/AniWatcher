package com.iskorsukov.aniwatcher.ui.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiringViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)
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

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
    }

    private fun initViewModel(testScheduler: TestCoroutineScheduler) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel = AiringViewModel(
            airingRepository,
            formatsFilterEventHandler,
            followEventHandler,
            resetStateEventHandler
        )
    }

    @After
    fun tearDown() {
        unmockkObject(DateTimeHelper)
    }

    @Test
    fun airingSchedulesByDayOfWeekFlow() = runTest {
        initViewModel(testScheduler)

        val result: Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>> =
            viewModel.airingSchedulesByDayOfWeekFlow.first()

        assertThat(result).isNotNull()
        assertThat(result.keys).containsExactlyElementsIn(
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
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()
    }

    @Test
    fun uiStateFlow() = runTest {
        initViewModel(testScheduler)

        assertThat(viewModel.uiStateFlow.value).isEqualTo(AiringUiState.DEFAULT)
    }

    @Test
    fun formatsFilterEvent_formatsFilterSelectionUpdated() = runTest {
        initViewModel(testScheduler)
        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )

        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        assertThat(viewModel.uiStateFlow.value.deselectedFormats).containsExactlyElementsIn(
            deselectedFormats
        )
        assertThat(viewModel.uiStateFlow.value.showReset).isTrue()

        verify {
            formatsFilterEventHandler.handleEvent(
                FormatsFilterSelectionUpdatedInputEvent(deselectedFormats),
                AiringUiState.DEFAULT
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
                AiringUiState.DEFAULT,
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
    }

    @Test
    fun resetStateEvent() = runTest {
        initViewModel(testScheduler)

        val deselectedFormats = listOf(
            MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE
        )
        viewModel.handleInputEvent(FormatsFilterSelectionUpdatedInputEvent(deselectedFormats))

        viewModel.handleInputEvent(ResetStateTriggeredInputEvent)

        assertThat(viewModel.uiStateFlow.value).isEqualTo(AiringUiState.DEFAULT)
    }
}