package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import com.iskorsukov.aniwatcher.ui.media.MediaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val formatsFilterEventHandler: FormatsFilterEventHandler<AiringUiState>,
    private val followEventHandler: FollowEventHandler<AiringUiState>,
    private val resetStateEventHandler: ResetStateEventHandler<AiringUiState>
) : ViewModel() {

    private val currentDayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

    private val _uiStateFlow: MutableStateFlow<AiringUiState> = MutableStateFlow(
        AiringUiState.DEFAULT
    )
    val uiStateFlow: StateFlow<AiringUiState> = _uiStateFlow

    val airingSchedulesByDayOfWeekFlow: Flow<Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>>> = airingRepository.mediaWithSchedulesFlow
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

    fun handleInputEvent(inputEvent: AiringInputEvent) {
        try {
            _uiStateFlow.value = when (inputEvent) {
                is FormatsFilterInputEvent -> formatsFilterEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
                )
                is FollowInputEvent -> followEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value,
                    viewModelScope,
                    airingRepository
                )
                is ResetStateTriggeredInputEvent -> resetStateEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
                )
                else -> throw IllegalArgumentException("Unsupported input event of type ${inputEvent::class.simpleName}")
            }
            updateResetButton()
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onError(ErrorItem.ofThrowable(e))
        }
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

    private fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }
}