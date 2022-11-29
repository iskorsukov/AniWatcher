package com.iskorsukov.aniwatcher.ui.base.viewmodel

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SortableViewModelDelegate @Inject constructor(): SortableViewModel {

    private val _sortingOptionFlow = MutableStateFlow(SortingOption.AIRING_AT)
    val sortingOptionFlow: StateFlow<SortingOption> = _sortingOptionFlow

    override fun onSortingOptionChanged(sortingOption: SortingOption) {
        _sortingOptionFlow.value = sortingOption
    }

    fun <T> sortMediaFlow(map: Map<MediaItem, T>, sortingOption: SortingOption): Map<MediaItem, T> {
        return map.toSortedMap(sortingOption.comparator)
    }
}