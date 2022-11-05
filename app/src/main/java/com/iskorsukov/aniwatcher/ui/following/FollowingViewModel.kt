package com.iskorsukov.aniwatcher.ui.following

import androidx.lifecycle.ViewModel
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    val followingMediaFlow by lazy {
        airingRepository.mediaWithSchedulesFlow.map { map ->
            MediaItemMapper.groupMediaWithNextAiringSchedule(map.filterKeys { it.isFollowing })
        }.distinctUntilChanged()
    }
}