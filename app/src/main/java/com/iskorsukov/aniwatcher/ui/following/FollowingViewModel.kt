package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<FollowingUiStateWithData> = MutableStateFlow(
        FollowingUiStateWithData()
    )
    val uiStateWithDataFlow: StateFlow<FollowingUiStateWithData> = _uiStateFlow
        .combine(airingRepository.timeInMinutesFlow) { uiStateWithData, timeInMinutes ->
            uiStateWithData.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .combine(airingRepository.followedMediaFlow) { uiStateWithData, mediaToSchedulesMap ->
            uiStateWithData.copy(
                mediaWithSchedulesMap = mediaToSchedulesMap
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, FollowingUiStateWithData())

    fun onFollowMedia(mediaItem: MediaItem) {
        viewModelScope.launch {
            try {
                if (mediaItem.isFollowing) {
                    airingRepository.unfollowMedia(mediaItem)
                } else {
                    airingRepository.followMedia(mediaItem)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
                onError(ErrorItem.ofThrowable(e))
            }
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }
}