package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val searchableViewModelDelegate: SearchableViewModelDelegate = SearchableViewModelDelegate(),
    private val sortableViewModelDelegate: SortableViewModelDelegate = SortableViewModelDelegate()
): FollowableMediaViewModel(airingRepository),
    SearchableViewModel by searchableViewModelDelegate,
    SortableViewModel by sortableViewModelDelegate {

    private val _errorItemFlow: MutableStateFlow<ErrorItem?> = MutableStateFlow(null)
    override val errorItemFlow: StateFlow<ErrorItem?> = _errorItemFlow

    val followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        MediaItemMapper.groupMediaWithNextAiringSchedule(map.filterKeys { it.isFollowing })
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
            try {
                airingRepository.unfollowMedia(finishedShows)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(throwable)
                _errorItemFlow.value = ErrorItem.ofThrowable(throwable)
            }
        }
    }
}