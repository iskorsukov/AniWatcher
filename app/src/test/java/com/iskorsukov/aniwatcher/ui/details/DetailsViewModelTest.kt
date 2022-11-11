package com.iskorsukov.aniwatcher.ui.details

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private val viewModel = DetailsViewModel(airingRepository)

    @Test
    fun get() = runTest {
        coEvery { airingRepository.getMediaWithAiringSchedules(any()) } returns flowOf(
            ModelTestDataCreator.baseMediaItem() to
                    ModelTestDataCreator.baseAiringScheduleItemList()
        )

        viewModel.getMediaWithAiringSchedules(1).first()

        coVerify {
            airingRepository.getMediaWithAiringSchedules(1)
        }
    }
}