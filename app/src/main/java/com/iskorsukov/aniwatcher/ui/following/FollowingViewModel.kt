package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.viewmodel.SortableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    val finishedFollowingShowsFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        val currentSeconds = System.currentTimeMillis() / 1000
        map
            .filterKeys { it.isFollowing }
            .filterValues { it.all { item -> item.airingAt < currentSeconds } }
            .keys
            .toList()
    }

    fun unfollowFinishedShows() {
        viewModelScope.launch {
            val finishedShows = finishedFollowingShowsFlow.first()
            airingRepository.unfollowMedia(finishedShows)
        }
    }
}