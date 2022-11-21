package com.iskorsukov.aniwatcher.ui.base

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.meanScore
import com.iskorsukov.aniwatcher.test.nextEpisodeAiringAt
import com.iskorsukov.aniwatcher.ui.base.viewmodel.SortableMediaViewModel
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SortableViewModelTest {

    private val firstItem = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(1) to
            ModelTestDataCreator.baseAiringScheduleItemList()
    private val secondItem = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(2).meanScore(2) to
            ModelTestDataCreator.baseAiringScheduleItemList()
    private val data = mapOf(firstItem, secondItem)

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).apply {
        coEvery { mediaWithSchedulesFlow } returns flowOf(data)
    }

    private val viewModel = object : SortableMediaViewModel(airingRepository) {
        val sortedMediaFlow = airingRepository.mediaWithSchedulesFlow
            .combine(sortingOptionFlow, this::sortMediaFlow)
    }

    @Test
    fun sortsMediaFlow() = runTest {
        var result: Map<MediaItem, List<AiringScheduleItem>> = viewModel.sortedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        viewModel.onSortingOptionChanged(SortingOption.SCORE)
        result = viewModel.sortedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()
    }
}