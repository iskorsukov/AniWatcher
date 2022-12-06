package com.iskorsukov.aniwatcher.ui.base.viewmodel.format

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

interface FormatFilterableViewModel {

    fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>)

    fun <T> filterFormatMediaFlow(map: Map<MediaItem, T>, deselectedFormats: List<MediaItem.LocalFormat>): Map<MediaItem, T> {
        return map.filterKeys { mediaItem -> !deselectedFormats.contains(mediaItem.format) }
    }
}