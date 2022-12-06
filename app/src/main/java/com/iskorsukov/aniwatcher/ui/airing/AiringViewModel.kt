package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableMediaViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.format.FormatFilterableViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    airingRepository: AiringRepository
) : FollowableMediaViewModel(airingRepository), FormatFilterableViewModel {

    private val currentDayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

    private val _uiStateFlow: MutableStateFlow<AiringUiState> = MutableStateFlow(
        AiringUiState.DEFAULT
    )
    val uiStateFlow: StateFlow<AiringUiState> = _uiStateFlow

    val airingSchedulesByDayOfWeekFlow = airingRepository.mediaWithSchedulesFlow
        .distinctUntilChanged()
        .combine(uiStateFlow) { map, uiState ->
            filterFormatMediaFlow(map, uiState.deselectedFormats)
        }
        .map {
            MediaItemMapper.groupAiringSchedulesByDayOfWeek(it).toSortedMap { first, second ->
                var firstDiff = first.ordinal - currentDayOfWeekLocal.ordinal
                if (firstDiff < 0) firstDiff += 7
                var secondDiff = second.ordinal - currentDayOfWeekLocal.ordinal
                if (secondDiff < 0) secondDiff += 7
                firstDiff - secondDiff
            }
        }

    override fun onDeselectedFormatsChanged(deselectedFormats: List<MediaItem.LocalFormat>) {
        _uiStateFlow.value = _uiStateFlow.value.copy(deselectedFormats = deselectedFormats)
        updateResetButton()
    }

    private fun updateResetButton() {
        val deselectedFormatsNotDefault =
            uiStateFlow.value.deselectedFormats != MediaUiState.DEFAULT.deselectedFormats
        if (deselectedFormatsNotDefault) {
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
        if (_uiStateFlow.value != AiringUiState.DEFAULT) {
            _uiStateFlow.value = AiringUiState.DEFAULT
        }
    }
}