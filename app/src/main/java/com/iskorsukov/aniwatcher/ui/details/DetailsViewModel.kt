package com.iskorsukov.aniwatcher.ui.details

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
class DetailsViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    private val _dataFlow: MutableStateFlow<DetailsScreenData> = MutableStateFlow(
        DetailsScreenData()
    )
    val dataFlow: StateFlow<DetailsScreenData> = _dataFlow
        .combine(airingRepository.timeInMinutesFlow) { data, timeInMinutes ->
            data.copy(
                timeInMinutes = timeInMinutes
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            DetailsScreenData()
        )

    fun loadMediaWithAiringSchedules(mediaItemId: Int) {
        _dataFlow.value = _dataFlow.value.copy(errorItem = null)
        viewModelScope.launch {
            try {
                airingRepository.getMediaWithAiringSchedules(mediaItemId).collect {
                    _dataFlow.value = _dataFlow.value.copy(
                        mediaItemWithSchedules = it
                    )
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