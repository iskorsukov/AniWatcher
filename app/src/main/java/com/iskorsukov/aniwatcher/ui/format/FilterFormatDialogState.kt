package com.iskorsukov.aniwatcher.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.iskorsukov.aniwatcher.domain.model.MediaItem

@Composable
fun rememberFilterFormatDialogState(): FilterFormatDialogState {
    return remember {
        FilterFormatDialogState()
    }
}

class FilterFormatDialogState {

    val deselectedFormats: MutableList<MediaItem.LocalFormat> = mutableStateListOf()

    var shouldShowFilterFormatDialog: Boolean by mutableStateOf(false)
        private set

    fun show() {
        shouldShowFilterFormatDialog = true
    }

    fun dismiss() {
        shouldShowFilterFormatDialog = false
    }
}