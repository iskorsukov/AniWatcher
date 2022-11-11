package com.iskorsukov.aniwatcher.ui.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiringViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)

    init {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY
    }

    private val viewModel = AiringViewModel(airingRepository)

    init {
        unmockkObject(DateTimeHelper)
    }

    @Test
    fun airingSchedulesByDayOfWeekFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flow {
            emit(
                mapOf(
                    ModelTestDataCreator.baseMediaItem() to
                            ModelTestDataCreator.baseAiringScheduleItemList()
                )
            )
        }

        var result: Map<DayOfWeekLocal, List<AiringScheduleItem>>? = null
        viewModel.airingSchedulesByDayOfWeekFlow.collectLatest { result = it }

        assertThat(result).isNotNull()
        assertThat(result!!.keys).containsExactlyElementsIn(listOf(
            DayOfWeekLocal.WEDNESDAY,
            DayOfWeekLocal.SATURDAY,
            DayOfWeekLocal.MONDAY
        )).inOrder()
        val list = ModelTestDataCreator.baseAiringScheduleItemList()
        val assertValues = listOf(
            list[1],
            list[3],
            list[0]
        )
        assertThat(result!!.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()

        unmockkObject(DateTimeHelper)
    }

    @Test
    fun onFollowMediaClicked() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val mediaItem = ModelTestDataCreator.baseMediaItem()

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.followMedia(mediaItem) }
    }

    @Test
    fun onFollowMediaClicked_unfollow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        val mediaItem = ModelTestDataCreator.baseMediaItem().isFollowing(true)

        viewModel.onFollowClicked(mediaItem)
        advanceUntilIdle()

        coVerify { airingRepository.unfollowMedia(mediaItem) }
    }
}