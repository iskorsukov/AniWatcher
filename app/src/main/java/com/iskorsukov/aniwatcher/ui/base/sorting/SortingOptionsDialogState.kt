package com.iskorsukov.aniwatcher.ui.base.sorting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberSortingOptionsDialogState(): SortingOptionsDialogState {
    return remember {
        SortingOptionsDialogState()
    }
}

class SortingOptionsDialogState {

    var selectedOption: SortingOption by mutableStateOf(SortingOption.AIRING_AT)

    var shouldShowSortingOptionsDialog: Boolean by mutableStateOf(false)
        private set

    fun show() {
        shouldShowSortingOptionsDialog = true
    }

    fun dismiss() {
        shouldShowSortingOptionsDialog = false
    }
}