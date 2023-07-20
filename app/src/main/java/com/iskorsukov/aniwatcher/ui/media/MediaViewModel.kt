package com.iskorsukov.aniwatcher.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    airingRepository: AiringRepository
) : ViewModel() {

    private val _dataFlow: MutableStateFlow<MediaScreenData> = MutableStateFlow(
        MediaScreenData()
    )
    val dataFlow: StateFlow<MediaScreenData> = _dataFlow
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
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            MediaScreenData()
        )
}