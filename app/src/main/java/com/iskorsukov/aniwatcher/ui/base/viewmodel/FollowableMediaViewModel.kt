package com.iskorsukov.aniwatcher.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class FollowableMediaViewModel(
    private val airingRepository: AiringRepository
) : ViewModel(), ErrorFlowViewModel {

    private val _errorItemFlow: MutableStateFlow<ErrorItem?> = MutableStateFlow(null)
    override val errorItemFlow: StateFlow<ErrorItem?> = _errorItemFlow

    fun onFollowClicked(mediaItem: MediaItem) {
        _errorItemFlow.value = null
        viewModelScope.launch {
            try {
                if (mediaItem.isFollowing) {
                    airingRepository.unfollowMedia(mediaItem)
                } else {
                    airingRepository.followMedia(mediaItem)
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(throwable)
                _errorItemFlow.value = ErrorItem.ofThrowable(throwable)
            }
        }
    }
}