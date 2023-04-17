package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.exception.RoomException
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.uistate.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FollowInputEvent: AiringInputEvent, DetailsInputEvent, FollowingInputEvent,
    MediaInputEvent
data class FollowClickedInputEvent(val mediaItem: MediaItem): FollowInputEvent

class FollowEventHandler<UiStateType: UiState> @Inject constructor() {

    fun handleEvent(
        followInputEvent: FollowInputEvent,
        originalUiState: UiStateType,
        coroutineScope: CoroutineScope,
        airingRepository: AiringRepository
    ): UiStateType {
        return when (followInputEvent) {
            is FollowClickedInputEvent -> {
                coroutineScope.launch {
                    val mediaItem = followInputEvent.mediaItem
                    try {
                        if (mediaItem.isFollowing) {
                            airingRepository.unfollowMedia(mediaItem)
                        } else {
                            airingRepository.followMedia(mediaItem)
                        }
                    } catch (e: Exception) {
                        throw RoomException(e)
                    }
                }
                originalUiState
            }
        }
    }
}