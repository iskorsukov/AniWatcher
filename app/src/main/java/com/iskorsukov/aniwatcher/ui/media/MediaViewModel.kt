package com.iskorsukov.aniwatcher.ui.media

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.ui.base.FollowableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): FollowableMediaViewModel(airingRepository)  {

    val mediaFlow by lazy {
        airingRepository.mediaWithSchedulesFlow.map {
            MediaItemMapper.groupMediaWithNextAiringSchedule(it)
        }.distinctUntilChanged()
    }
}