package com.iskorsukov.aniwatcher.ui.details

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModelDelegate
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
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val followableViewModelDelegate: FollowableViewModelDelegate = mockk(relaxed = true)

    private lateinit var viewModel: DetailsViewModel

    @Test
    fun getMediaWithAiringSchedules() = runTest {
        coEvery { airingRepository.getMediaWithAiringSchedules(any()) } returns flowOf(
            ModelTestDataCreator.baseMediaItem to
                    ModelTestDataCreator.baseAiringScheduleItemList()
        )
        viewModel = DetailsViewModel(airingRepository, settingsRepository, followableViewModelDelegate)

        viewModel.getMediaWithAiringSchedules(1).first()

        coVerify {
            airingRepository.getMediaWithAiringSchedules(1)
        }
    }
}