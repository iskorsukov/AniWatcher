package com.iskorsukov.aniwatcher.ui.base.viewmodel.follow

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.error.ErrorFlowViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowableViewModelDelegate @Inject constructor() {
    fun onFollowClicked(
        mediaItem: MediaItem,
        scope: CoroutineScope,
        airingRepository: AiringRepository,
        errorFlowViewModel: ErrorFlowViewModel
    ) {
        errorFlowViewModel.onError(null)
        scope.launch {
            try {
                if (mediaItem.isFollowing) {
                    airingRepository.unfollowMedia(mediaItem)
                } else {
                    airingRepository.followMedia(mediaItem)
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(throwable)
                errorFlowViewModel.onError(ErrorItem.ofThrowable(throwable))
            }
        }
    }
}