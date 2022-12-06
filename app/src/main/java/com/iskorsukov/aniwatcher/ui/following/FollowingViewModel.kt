package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableMediaViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.format.FormatFilterableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.base.viewmodel.sort.SortableViewModel
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val searchableViewModelDelegate: SearchableViewModelDelegate = SearchableViewModelDelegate()
): FollowableMediaViewModel(airingRepository),
    SearchableViewModel by searchableViewModelDelegate,
    SortableViewModel,
    FormatFilterableViewModel {

    private val _uiStateFlow: MutableStateFlow<FollowingUiState> = MutableStateFlow(
        FollowingUiState.DEFAULT
    )
    val uiStateFlow: StateFlow<FollowingUiState> = _uiStateFlow

    val followingMediaFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        MediaItemMapper.groupMediaWithNextAiringSchedule(map.filterKeys { it.isFollowing })
    }
        .distinctUntilChanged()
        .combine(
            searchableViewModelDelegate.searchTextFlow,
            searchableViewModelDelegate::filterMediaFlow
        )
        .combine(uiStateFlow) { map, uiState ->
            filterFormatMediaFlow(map, uiState.deselectedFormats)
        }
        .combine(uiStateFlow) { map, uiState ->
            sortMediaFlow(map, uiState.sortingOption)
        }

    val finishedFollowingShowsFlow = airingRepository.mediaWithSchedulesFlow.map { map ->
        val currentSeconds = System.currentTimeMillis() / 1000
        map
            .filterKeys { it.isFollowing }
            .filterValues { it.all { item -> item.airingAt < currentSeconds } }
            .keys
            .toList()
    }

    fun unfollowFinishedShows() {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = null)
        viewModelScope.launch {
            val finishedShows = finishedFollowingShowsFlow.first()
            try {
                airingRepository.unfollowMedia(finishedShows)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(throwable)
                _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = ErrorItem.ofThrowable(throwable))
            }
        }
    }

    override fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>) {
        _uiStateFlow.value = _uiStateFlow.value.copy(deselectedFormats = deselectedFormats)
        updateResetButton()
    }

    override fun onSortingOptionChanged(sortingOption: SortingOption) {
        _uiStateFlow.value = _uiStateFlow.value.copy(sortingOption = sortingOption)
        updateResetButton()
    }

    private fun updateResetButton() {
        val deselectedFormatsNotDefault = uiStateFlow.value.deselectedFormats != FollowingUiState.DEFAULT.deselectedFormats
        val sortingOptionNotDefault = uiStateFlow.value.sortingOption != FollowingUiState.DEFAULT.sortingOption
        if (deselectedFormatsNotDefault || sortingOptionNotDefault) {
            if (!uiStateFlow.value.showReset) {
                _uiStateFlow.value = _uiStateFlow.value.copy(showReset = true)
            }
        } else {
            if (uiStateFlow.value.showReset) {
                _uiStateFlow.value = _uiStateFlow.value.copy(showReset = false)
            }
        }
    }

    fun resetState() {
        if (_uiStateFlow.value != FollowingUiState.DEFAULT) {
            _uiStateFlow.value = FollowingUiState.DEFAULT
        }
    }
}