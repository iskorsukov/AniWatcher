package com.iskorsukov.aniwatcher.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.BuildConfig
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val airingRepository: AiringRepository
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

    fun onFollowMedia(mediaItem: MediaItem) {
        viewModelScope.launch {
            try {
                if (mediaItem.isFollowing) {
                    airingRepository.unfollowMedia(mediaItem)
                } else {
                    airingRepository.followMedia(mediaItem)
                }
            } catch (e: Exception) {
                if (!BuildConfig.DEBUG) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
                e.printStackTrace()
                onError(ErrorItem.ofThrowable(e))
            }
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _dataFlow.value = _dataFlow.value.copy(errorItem = errorItem)
    }
}