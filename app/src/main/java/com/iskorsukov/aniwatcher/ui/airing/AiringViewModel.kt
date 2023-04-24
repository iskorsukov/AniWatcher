package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.AiringInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowEventHandler
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterEventHandler
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateEventHandler
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateTriggeredInputEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val mediaItemMapper: MediaItemMapper,
    private val formatsFilterEventHandler: FormatsFilterEventHandler<AiringUiState>,
    private val followEventHandler: FollowEventHandler<AiringUiState>,
    private val resetStateEventHandler: ResetStateEventHandler<AiringUiState>
) : ViewModel() {

    private val currentDayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

    private val _uiStateFlow: MutableStateFlow<AiringUiState> = MutableStateFlow(
        AiringUiState.DEFAULT
    )
    val uiStateWithDataFlow: StateFlow<AiringUiStateWithData> = _uiStateFlow
        .map { uiState ->
            AiringUiStateWithData(uiState = uiState)
        }
        .combine(airingRepository.timeInMinutesFlow) { uiStateWithData, timeInMinutes ->
            uiStateWithData.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.mediaWithSchedulesFlow) { uiStateWithData, mediaToSchedulesMap ->
            val filteredMediaMap =
                filterFormatMediaFlow(mediaToSchedulesMap, uiStateWithData.uiState.deselectedFormats)
            val groupedMediaMap = mediaItemMapper.groupAiringSchedulesByDayOfWeek(
                filteredMediaMap,
                uiStateWithData.timeInMinutes
            ).toSortedMap { first, second ->
                var firstDiff = first.ordinal - currentDayOfWeekLocal.ordinal
                if (firstDiff < 0) firstDiff += 7
                var secondDiff = second.ordinal - currentDayOfWeekLocal.ordinal
                if (secondDiff < 0) secondDiff += 7
                firstDiff - secondDiff
            }
            uiStateWithData.copy(
                schedulesByDayOfWeek = groupedMediaMap
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, AiringUiStateWithData())

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
            _uiStateFlow.value.deselectedFormats != AiringUiState.DEFAULT.deselectedFormats
        if (deselectedFormatsNotDefault) {
            if (!_uiStateFlow.value.showReset) {
                _uiStateFlow.value = _uiStateFlow.value.copy(showReset = true)
            }
        } else {
            if (_uiStateFlow.value.showReset) {
                _uiStateFlow.value = _uiStateFlow.value.copy(showReset = false)
            }
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }
}