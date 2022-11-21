package com.iskorsukov.aniwatcher.ui.base.viewmodel

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import kotlinx.coroutines.flow.MutableStateFlow

open class SortableMediaViewModel(
    airingRepository: AiringRepository
): SearchableMediaViewModel(airingRepository) {

    protected val sortingOptionFlow: MutableStateFlow<SortingOption> = MutableStateFlow(SortingOption.AIRING_AT)

    fun onSortingOptionChanged(sortingOption: SortingOption) {
        sortingOptionFlow.value = sortingOption
    }

    protected fun <T> sortMediaFlow(map: Map<MediaItem, T>, sortingOption: SortingOption): Map<MediaItem, T> {
        return map.toSortedMap(sortingOption.comparator)
    }
}