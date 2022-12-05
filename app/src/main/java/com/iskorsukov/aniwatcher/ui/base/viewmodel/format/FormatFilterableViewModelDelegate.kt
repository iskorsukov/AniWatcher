package com.iskorsukov.aniwatcher.ui.base.viewmodel.format

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FormatFilterableViewModelDelegate @Inject constructor(): FormatFilterableViewModel {

    private val _deselectedFormatsFlow: MutableStateFlow<List<MediaItem.LocalFormat>> =
        MutableStateFlow(
            emptyList()
        )
    override val deselectedFormatsFlow: StateFlow<List<MediaItem.LocalFormat>> =
        _deselectedFormatsFlow

    override fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>) {
        _deselectedFormatsFlow.value = deselectedFormats
    }

    fun filterFormatMediaFlow(map: Map<MediaItem, AiringScheduleItem?>, deselectedFormats: List<MediaItem.LocalFormat>): Map<MediaItem, AiringScheduleItem?> {
        return map.filterKeys { mediaItem -> !deselectedFormats.contains(mediaItem.format) }
    }
}