package com.iskorsukov.aniwatcher.ui.media

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
class MediaViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    private val mediaItemMapper: MediaItemMapper,
    private val followEventHandler: FollowEventHandler<MediaUiState>,
    private val searchTextEventHandler: SearchTextEventHandler<MediaUiState>,
    private val formatsFilterEventHandler: FormatsFilterEventHandler<MediaUiState>,
    private val sortingOptionEventHandler: SortingOptionEventHandler<MediaUiState>,
    private val resetStateEventHandler: ResetStateEventHandler<MediaUiState>
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<MediaUiState> = MutableStateFlow(
        MediaUiState.DEFAULT
    )
    val uiStateWithDataFlow: StateFlow<MediaUiStateWithData> = _uiStateFlow
        .map { uiState: MediaUiState ->
            MediaUiStateWithData(uiState = uiState)
        }
        .combine(airingRepository.timeInMinutesFlow) { uiState, timeInMinutes ->
            uiState.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.mediaWithSchedulesFlow) { uiStateWithData, mediaWithSchedulesMap ->
            val groupedMediaMap = mediaItemMapper.groupMediaWithNextAiringSchedule(
                mediaWithSchedulesMap,
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
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            MediaUiStateWithData()
        )

    fun handleInputEvent(inputEvent: MediaInputEvent) {
        try {
            _uiStateFlow.value = when (inputEvent) {
                is FollowInputEvent -> followEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value,
                    viewModelScope,
                    airingRepository
                )

                is SearchTextInputEvent -> searchTextEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value
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
            _uiStateFlow.value.deselectedFormats != MediaUiState.DEFAULT.deselectedFormats
        val sortingOptionNotDefault =
            _uiStateFlow.value.sortingOption != MediaUiState.DEFAULT.sortingOption
        val searchTextNotDefault =
            _uiStateFlow.value.searchText != MediaUiState.DEFAULT.searchText
        if (deselectedFormatsNotDefault || sortingOptionNotDefault || searchTextNotDefault) {
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