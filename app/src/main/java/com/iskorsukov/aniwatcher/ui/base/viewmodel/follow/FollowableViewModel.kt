package com.iskorsukov.aniwatcher.ui.base.viewmodel.follow

import com.iskorsukov.aniwatcher.domain.model.MediaItem

interface FollowableViewModel {
    fun onFollowClicked(mediaItem: MediaItem)
}