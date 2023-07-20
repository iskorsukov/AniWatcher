package com.iskorsukov.aniwatcher.ui.airing

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
class AiringViewModel @Inject constructor(
    airingRepository: AiringRepository,
) : ViewModel() {

    private val _dataFlow: MutableStateFlow<AiringScreenData> = MutableStateFlow(
        AiringScreenData()
    )
    val dataFlow: StateFlow<AiringScreenData> = _dataFlow
        .combine(airingRepository.timeInMinutesFlow) { data, timeInMinutes ->
            data.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.mediaWithSchedulesFlow) { data, mediaWithSchedulesMap ->
            data.copy(
                mediaWithSchedulesMap = mediaWithSchedulesMap
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily, AiringScreenData()
        )
}