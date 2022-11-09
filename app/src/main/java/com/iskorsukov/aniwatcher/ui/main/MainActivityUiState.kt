package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.ui.base.ErrorItem

data class MainActivityUiState(
    val isRefreshing: Boolean,
    val errorItem: ErrorItem?
)