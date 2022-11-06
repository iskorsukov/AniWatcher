package com.iskorsukov.aniwatcher.ui.airing

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiringViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private val viewModel = AiringViewModel(airingRepository)

    @Test
    fun loadAiringData() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentYear(any()) } returns 2022
        every { DateTimeHelper.currentSeason(any()) } returns "FALL"

        viewModel.loadAiringData()
        advanceUntilIdle()

        coVerify { airingRepository.loadSeasonAiringData(2022, "FALL") }

        unmockkAll()
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
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.WEDNESDAY,
            DayOfWeekLocal.SATURDAY
        )).inOrder()
        val list = ModelTestDataCreator.baseAiringScheduleItemList()
        val assertValues = listOf(
            list[0],
            list[1],
            list[3]
        )
        assertThat(result!!.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()
    }
}