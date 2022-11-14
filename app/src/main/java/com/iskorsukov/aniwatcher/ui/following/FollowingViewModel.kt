package com.iskorsukov.aniwatcher.ui.following

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.FollowableMediaViewModel
import com.iskorsukov.aniwatcher.ui.base.SearchableMediaViewModel
import com.iskorsukov.aniwatcher.ui.base.SortableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): SortableMediaViewModel(airingRepository) {

    val followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        MediaItemMapper.groupMediaWithNextAiringSchedule(map.filterKeys { it.isFollowing })
    }
        .distinctUntilChanged()
        .combine(searchTextFlow, this::filterMediaFlow)
        .combine(sortingOptionFlow, this::sortMediaFlow)
}