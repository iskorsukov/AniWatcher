package com.iskorsukov.aniwatcher.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.error.ErrorFlowViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModel
import com.iskorsukov.aniwatcher.ui.base.viewmodel.follow.FollowableViewModelDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    settingsRepository: SettingsRepository,
    private val followableViewModelDelegate: FollowableViewModelDelegate
): ViewModel(), FollowableViewModel, ErrorFlowViewModel {

    private val _errorItemFlow: MutableStateFlow<ErrorItem?> = MutableStateFlow(null)
    val errorItemFlow: StateFlow<ErrorItem?> = _errorItemFlow

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    fun getMediaWithAiringSchedules(mediaItemId: Int): Flow<Pair<MediaItem, List<AiringScheduleItem>>?> {
        return airingRepository.getMediaWithAiringSchedules(mediaItemId)
    }

    override fun onError(errorItem: ErrorItem?) {
        _errorItemFlow.value = errorItem
    }

    override fun onFollowClicked(mediaItem: MediaItem) {
        followableViewModelDelegate.onFollowClicked(
            mediaItem,
            viewModelScope,
            airingRepository,
            this
        )
    }
}