package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextUiState
import java.util.concurrent.TimeUnit

data class MainActivityUiState(
    val isRefreshing: Boolean = false,
    val errorItem: ErrorItem? = null,
    val unreadNotificationsCount: Int = 0
)