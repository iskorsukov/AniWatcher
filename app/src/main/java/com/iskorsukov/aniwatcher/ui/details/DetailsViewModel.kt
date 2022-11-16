package com.iskorsukov.aniwatcher.ui.details

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.ui.base.FollowableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    settingsRepository: SettingsRepository
): FollowableMediaViewModel(airingRepository) {

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>> {
        return airingRepository.getMediaWithAiringSchedules(mediaItemId)
    }
}