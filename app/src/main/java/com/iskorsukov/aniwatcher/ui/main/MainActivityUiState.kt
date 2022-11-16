package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.ui.base.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class MainActivityUiState(
    val isRefreshing: Boolean,
    val errorItem: ErrorItem? = null,
    val showSortingOptionsDialog: Boolean = false,
    val searchText: String = "",
    val sortingOption: SortingOption = SortingOption.AIRING_AT
)