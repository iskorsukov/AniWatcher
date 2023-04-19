package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.filterSearchMediaFlow
import com.iskorsukov.aniwatcher.ui.base.util.sortMediaFlow
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val mediaItemMapper: MediaItemMapper,
    private val searchTextEventHandler: SearchTextEventHandler<FollowingUiState>,
    private val followEventHandler: FollowEventHandler<FollowingUiState>,
    private val sortingOptionEventHandler: SortingOptionEventHandler<FollowingUiState>,
    private val formatsFilterEventHandler: FormatsFilterEventHandler<FollowingUiState>,
    private val resetStateEventHandler: ResetStateEventHandler<FollowingUiState>
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<FollowingUiState> = MutableStateFlow(
        FollowingUiState.DEFAULT
    )
    val uiStateWithDataFlow: StateFlow<FollowingUiStateWithData> = _uiStateFlow
        .map { uiState ->
            FollowingUiStateWithData(uiState = uiState)
        }
        .combine(airingRepository.timeInMinutesFlow) { uiStateWithData, timeInMinutes ->
            uiStateWithData.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.followedMediaFlow) { uiStateWithData, mediaToSchedulesMap ->
            val groupedMediaMap = mediaItemMapper.groupMediaWithNextAiringSchedule(
                mediaToSchedulesMap,
                uiStateWithData.timeInMinutes
            )
            val filteredBySearchMap = filterSearchMediaFlow(groupedMediaMap, uiStateWithData.uiState.searchText)
            val filteredByFormatMap =
                filterFormatMediaFlow(filteredBySearchMap, uiStateWithData.uiState.deselectedFormats)
            val sortedMediaMap = sortMediaFlow(filteredByFormatMap, uiStateWithData.uiState.sortingOption)
            uiStateWithData.copy(
                mediaWithNextAiringMap = sortedMediaMap
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, FollowingUiStateWithData())

    fun handleInputEvent(inputEvent: FollowingInputEvent) {
        try {
            _uiStateFlow.value = when (inputEvent) {
                is SearchTextInputEvent -> searchTextEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
                )

                is FollowInputEvent -> followEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value,
                    viewModelScope,
                    airingRepository
                )

                is SortingOptionInputEvent -> sortingOptionEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
                )

                is FormatsFilterInputEvent -> formatsFilterEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
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
            _uiStateFlow.value.deselectedFormats != FollowingUiState.DEFAULT.deselectedFormats
        val sortingOptionNotDefault =
            _uiStateFlow.value.sortingOption != FollowingUiState.DEFAULT.sortingOption
        if (deselectedFormatsNotDefault || sortingOptionNotDefault) {
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