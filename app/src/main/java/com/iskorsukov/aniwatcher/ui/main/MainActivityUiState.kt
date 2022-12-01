package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import java.util.*

data class MainActivityUiState(
    val isRefreshing: Boolean,
    val seasonYear: DateTimeHelper.SeasonYear =
        DateTimeHelper.currentSeasonYear(Calendar.getInstance()),
    val errorItem: ErrorItem? = null,
    val searchText: String = "",
    val searchFieldOpen: Boolean = false,
    val sortingOption: SortingOption = SortingOption.AIRING_AT,
)