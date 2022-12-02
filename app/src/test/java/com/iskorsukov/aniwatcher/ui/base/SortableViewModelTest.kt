package com.iskorsukov.aniwatcher.ui.base

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.airingAt
import com.iskorsukov.aniwatcher.test.meanScore
import com.iskorsukov.aniwatcher.ui.base.viewmodel.sort.SortableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SortableViewModelTest {

    private val firstItem = ModelTestDataCreator.baseMediaItem() to
            listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(1))
    private val secondItem = ModelTestDataCreator.baseMediaItem().meanScore(2) to
            listOf(ModelTestDataCreator.baseAiringScheduleItem().airingAt(2))
    private val data = mapOf(firstItem, secondItem)

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).apply {
        coEvery { mediaWithSchedulesFlow } returns flowOf(data)
    }

    private val sortableViewModelDelegate = SortableViewModelDelegate()
    private val sortedMediaFlow = airingRepository.mediaWithSchedulesFlow
            .map { MediaItemMapper.groupMediaWithNextAiringSchedule(it) }
            .combine(
                sortableViewModelDelegate.sortingOptionFlow,
                sortableViewModelDelegate::sortMediaFlow
            )

    @Test
    fun sortsMediaFlow() = runTest {
        var result = sortedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(firstItem.first, secondItem.first).inOrder()

        sortableViewModelDelegate.onSortingOptionChanged(SortingOption.SCORE)
        result = sortedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactly(secondItem.first, firstItem.first).inOrder()
    }
}