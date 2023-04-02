package com.iskorsukov.aniwatcher.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.error.ErrorFlowViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.base.viewmodel.format.FormatFilterableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.search.SearchableViewModelDelegate
import com.iskorsukov.aniwatcher.ui.base.viewmodel.sort.SortableViewModel
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val searchableViewModelDelegate: SearchableViewModelDelegate,
    private val followableViewModelDelegate: FollowableViewModelDelegate
): ViewModel(),
    FollowableViewModel,
    ErrorFlowViewModel,
    SearchableViewModel by searchableViewModelDelegate,
    SortableViewModel,
    FormatFilterableViewModel {

    private val _uiStateFlow: MutableStateFlow<MediaUiState> = MutableStateFlow(
        MediaUiState.DEFAULT
    )
    val uiStateFlow: StateFlow<MediaUiState> = _uiStateFlow

    val mediaFlow = airingRepository.mediaWithSchedulesFlow.map {
        MediaItemMapper.groupMediaWithNextAiringSchedule(it)
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

    override fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }

    override fun onFollowClicked(mediaItem: MediaItem) {
        followableViewModelDelegate.onFollowClicked(
            mediaItem,
            viewModelScope,
            airingRepository,
            this::onError
        )
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
        val deselectedFormatsNotDefault = uiStateFlow.value.deselectedFormats != MediaUiState.DEFAULT.deselectedFormats
        val sortingOptionNotDefault = uiStateFlow.value.sortingOption != MediaUiState.DEFAULT.sortingOption
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
        if (_uiStateFlow.value != MediaUiState.DEFAULT) {
            _uiStateFlow.value = MediaUiState.DEFAULT
        }
    }
}