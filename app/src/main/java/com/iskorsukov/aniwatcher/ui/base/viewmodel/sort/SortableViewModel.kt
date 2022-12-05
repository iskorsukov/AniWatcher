package com.iskorsukov.aniwatcher.ui.base.viewmodel.sort

import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import kotlinx.coroutines.flow.StateFlow

interface SortableViewModel {

    val sortingOptionFlow: StateFlow<SortingOption>

    fun onSortingOptionChanged(sortingOption: SortingOption)
}