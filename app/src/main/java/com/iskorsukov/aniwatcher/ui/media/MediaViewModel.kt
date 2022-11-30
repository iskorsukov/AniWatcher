package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableMediaViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.base.viewmodel.sort.SortableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.sort.SortableViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    airingRepository: AiringRepository,
    private val searchableViewModelDelegate: SearchableViewModelDelegate = SearchableViewModelDelegate(),
    private val sortableViewModelDelegate: SortableViewModelDelegate = SortableViewModelDelegate()
): FollowableMediaViewModel(airingRepository),
    SearchableViewModel by searchableViewModelDelegate,
    SortableViewModel by sortableViewModelDelegate {

    val mediaFlow = airingRepository.mediaWithSchedulesFlow.map {
        MediaItemMapper.groupMediaWithNextAiringSchedule(it)
    }
        .distinctUntilChanged()
        .combine(
            searchableViewModelDelegate.searchTextFlow,
            searchableViewModelDelegate::filterMediaFlow
        )
        .combine(
            sortableViewModelDelegate.sortingOptionFlow,
            sortableViewModelDelegate::sortMediaFlow
        )
}