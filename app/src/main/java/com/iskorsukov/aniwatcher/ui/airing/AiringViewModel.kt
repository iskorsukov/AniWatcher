package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.BuildConfig
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
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