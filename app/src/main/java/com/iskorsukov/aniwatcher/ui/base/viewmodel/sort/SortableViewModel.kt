package com.iskorsukov.aniwatcher.ui.base.viewmodel.sort

import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

interface SortableViewModel {
    fun onSortingOptionChanged(sortingOption: SortingOption)
}