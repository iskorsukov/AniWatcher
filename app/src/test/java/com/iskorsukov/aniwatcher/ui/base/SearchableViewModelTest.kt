package com.iskorsukov.aniwatcher.ui.base

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.viewmodel.SearchableMediaViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchableViewModelTest {

    private val firstItem = ModelTestDataCreator.baseMediaItem() to
            ModelTestDataCreator.baseAiringScheduleItemList()
    private val secondItem = ModelTestDataCreator.baseMediaItem().id(2)
        .title(MediaItem.Title(null, "SearchText", "Title")) to
            ModelTestDataCreator.baseAiringScheduleItemList()
    private val data = mapOf(firstItem, secondItem)

    private val airingRepository: AiringRepository = mockk<AiringRepository>(relaxed = true).apply {
        coEvery { mediaWithSchedulesFlow } returns flowOf(data)
    }

    private val viewModel = object : SearchableMediaViewModel(airingRepository) {
        val searchedMediaFlow = airingRepository.mediaWithSchedulesFlow
            .combine(searchTextFlow, this::filterMediaFlow)
    }

    @Test
    fun filtersFlowBySearchText() = runTest {
        var result: Map<MediaItem, List<AiringScheduleItem>> = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactlyElementsIn(data.keys)

        viewModel.onSearchTextChanged("SearchText")
        result = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(secondItem.first)
    }

    @Test
    fun filtersFlowBySearchText_multiple() = runTest {
        var result: Map<MediaItem, List<AiringScheduleItem>> = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactlyElementsIn(data.keys)

        viewModel.onSearchTextChanged("Search Title")
        result = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(1)
        assertThat(result.keys).containsExactly(secondItem.first)
    }

    @Test
    fun needsAtLeast4Characters() = runTest {
        var result: Map<MediaItem, List<AiringScheduleItem>> = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactlyElementsIn(data.keys)

        viewModel.onSearchTextChanged("Sea")
        result = viewModel.searchedMediaFlow.first()

        assertThat(result.size).isEqualTo(2)
        assertThat(result.keys).containsExactlyElementsIn(data.keys)
    }
}