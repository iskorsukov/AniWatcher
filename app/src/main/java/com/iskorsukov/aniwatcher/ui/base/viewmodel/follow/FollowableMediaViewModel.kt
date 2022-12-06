package com.iskorsukov.aniwatcher.ui.base.viewmodel.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.error.ErrorFlowViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class FollowableMediaViewModel(
    private val airingRepository: AiringRepository
) : ViewModel(), ErrorFlowViewModel {

    fun onFollowClicked(mediaItem: MediaItem) {
        onError(null)
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
                onError(ErrorItem.ofThrowable(throwable))
            }
        }
    }
}