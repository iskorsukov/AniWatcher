package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.exception.RoomException
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<AiringUiStateWithData> = MutableStateFlow(
        AiringUiStateWithData()
    )
    val uiStateWithDataFlow: StateFlow<AiringUiStateWithData> = _uiStateFlow
        .combine(airingRepository.timeInMinutesFlow) { uiState, timeInMinutes ->
            uiState.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.mediaWithSchedulesFlow) { uiStateWithData, mediaWithSchedulesMap ->
            uiStateWithData.copy(
                mediaWithSchedulesMap = mediaWithSchedulesMap
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, AiringUiStateWithData())

    fun onFollowMedia(mediaItem: MediaItem) {
        viewModelScope.launch {
            try {
                if (mediaItem.isFollowing) {
                    airingRepository.unfollowMedia(mediaItem)
                } else {
                    airingRepository.followMedia(mediaItem)
                }
            } catch (e: Exception) {
                throw RoomException(e)
            }
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }
}