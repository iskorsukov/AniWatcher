package com.iskorsukov.aniwatcher.ui.details

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowEventHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val airingRepository: AiringRepository = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val followEventHandler: FollowEventHandler<DetailsUiState> = spyk(FollowEventHandler())

    private lateinit var viewModel: DetailsViewModel

    private val testData = ModelTestDataCreator.baseMediaItem to
            ModelTestDataCreator.baseAiringScheduleItemList()

    @Test
    fun uiStateFlow() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { airingRepository.getMediaWithAiringSchedules(any()) } returns flowOf(
            testData
        )
        viewModel = DetailsViewModel(airingRepository, settingsRepository, followEventHandler)
        viewModel.loadMediaWithAiringSchedules(1)
        advanceUntilIdle()

        val uiState = viewModel.uiStateFlow.first()

        assertThat(uiState.mediaItemWithSchedules).isEqualTo(testData)

        coVerify {
            airingRepository.getMediaWithAiringSchedules(1)
        }
    }
}