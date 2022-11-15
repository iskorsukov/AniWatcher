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

    private lateinit var viewModel: AiringViewModel

    @Test
    fun airingSchedulesByDayOfWeekFlow() = runTest {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentDayOfWeek() } returns DayOfWeekLocal.WEDNESDAY

        coEvery { airingRepository.mediaWithSchedulesFlow } returns flowOf(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
        viewModel = AiringViewModel(airingRepository)

        val result: Map<DayOfWeekLocal, List<AiringScheduleItem>> =
            viewModel.airingSchedulesByDayOfWeekFlow.first()

        assertThat(result).isNotNull()
        assertThat(result.keys).containsExactlyElementsIn(listOf(
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
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues).inOrder()

        unmockkObject(DateTimeHelper)
    }
}