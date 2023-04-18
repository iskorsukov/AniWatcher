package com.iskorsukov.aniwatcher.ui.main

import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextUiState
import java.util.concurrent.TimeUnit

data class MainActivityUiState(
    val isRefreshing: Boolean,
    val errorItem: ErrorItem? = null,
    override val searchText: String = "",
    val searchFieldOpen: Boolean = false,
    val unreadNotificationsCount: Int = 0,
    val showNotificationsPermissionRationale: Boolean = false,
    val launchNotificationPermissionRequest: Boolean = false,
    val notificationsPermissionGranted: Boolean = true
): SearchTextUiState {

    companion object {
        val DEFAULT = MainActivityUiState(isRefreshing = false)
    }
    override fun copyWithSearchTextStateUpdated(
        searchText: String?,
        isSearchFieldOpen: Boolean?
    ): SearchTextUiState {
        return if (searchText == null && isSearchFieldOpen == null) {
            this
        } else {
            this.copy(
                searchText = searchText ?: this.searchText,
                searchFieldOpen = isSearchFieldOpen ?: this.searchFieldOpen
            )
        }
    }
}