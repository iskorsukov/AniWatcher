package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.viewmodel.SortableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    airingRepository: AiringRepository
) : SortableMediaViewModel(airingRepository) {

    val mediaFlow = airingRepository.mediaWithSchedulesFlow.map {
        MediaItemMapper.groupMediaWithNextAiringSchedule(it)
    }
        .distinctUntilChanged()
        .combine(searchTextFlow, this::filterMediaFlow)
        .combine(sortingOptionFlow, this::sortMediaFlow)
}