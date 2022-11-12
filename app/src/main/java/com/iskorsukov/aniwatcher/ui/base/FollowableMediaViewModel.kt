package com.iskorsukov.aniwatcher.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.launch

open class FollowableMediaViewModel(
    private val airingRepository: AiringRepository
) : ViewModel() {

    fun onFollowClicked(mediaItem: MediaItem) {
        viewModelScope.launch {
            if (mediaItem.isFollowing) {
                airingRepository.unfollowMedia(mediaItem)
            } else {
                airingRepository.followMedia(mediaItem)
            }
        }
    }
}