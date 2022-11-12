package com.iskorsukov.aniwatcher.ui.details

import com.iskorsukov.aniwatcher.domain.airing.AiringRepositoryImpl
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

    private val airingRepositoryImpl: AiringRepositoryImpl = mockk(relaxed = true)

    private val viewModel = DetailsViewModel(airingRepositoryImpl)

    @Test
    fun get() = runTest {
        coEvery { airingRepositoryImpl.getMediaWithAiringSchedules(any()) } returns flowOf(
            ModelTestDataCreator.baseMediaItem() to
                    ModelTestDataCreator.baseAiringScheduleItemList()
        )

        viewModel.getMediaWithAiringSchedules(1).first()

        coVerify {
            airingRepositoryImpl.getMediaWithAiringSchedules(1)
        }
    }
}