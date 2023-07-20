package com.iskorsukov.aniwatcher.ui.following

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
class FollowingViewModel @Inject constructor(
    airingRepository: AiringRepository
) : ViewModel() {

    private val _dataFlow: MutableStateFlow<FollowingScreenData> = MutableStateFlow(
        FollowingScreenData()
    )
    val dataFlow: StateFlow<FollowingScreenData> = _dataFlow
        .combine(airingRepository.timeInMinutesFlow) { data, timeInMinutes ->
            data.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.followedMediaFlow) { data, mediaToSchedulesMap ->
            data.copy(
                mediaWithSchedulesMap = mediaToSchedulesMap
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            FollowingScreenData()
        )
}