package com.iskorsukov.aniwatcher.ui.base.viewmodel.format

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

interface FormatFilterableViewModel {

    val deselectedFormatsFlow: StateFlow<List<MediaItem.LocalFormat>>

    fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>)
}