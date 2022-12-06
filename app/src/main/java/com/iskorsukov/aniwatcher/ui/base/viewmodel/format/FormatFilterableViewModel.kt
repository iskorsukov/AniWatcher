package com.iskorsukov.aniwatcher.ui.base.viewmodel.format

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

interface FormatFilterableViewModel {

    fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>)

    fun filterFormatMediaFlow(map: Map<MediaItem, AiringScheduleItem?>, deselectedFormats: List<MediaItem.LocalFormat>): Map<MediaItem, AiringScheduleItem?> {
        return map.filterKeys { mediaItem -> !deselectedFormats.contains(mediaItem.format) }
    }
}