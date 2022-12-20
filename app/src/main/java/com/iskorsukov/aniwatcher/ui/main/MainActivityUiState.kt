package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

data class MainActivityUiState(
    val isRefreshing: Boolean,
    val errorItem: ErrorItem? = null,
    val searchText: String = "",
    val searchFieldOpen: Boolean = false
)