package com.iskorsukov.aniwatcher.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.DetailsInputEvent
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowEventHandler
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FollowInputEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val airingRepository: AiringRepository,
    settingsRepository: SettingsRepository,
    private val followEventHandler: FollowEventHandler<DetailsUiState>
): ViewModel() {

    private val _uiStateFlow: MutableStateFlow<DetailsUiState> = MutableStateFlow(DetailsUiState.DEFAULT)
    val uiStateFlow: StateFlow<DetailsUiState> = _uiStateFlow

    val settingsState: StateFlow<SettingsState> = settingsRepository.settingsStateFlow

    fun loadMediaWithAiringSchedules(mediaItemId: Int) {
        viewModelScope.launch {
            airingRepository.getMediaWithAiringSchedules(mediaItemId).collect {
                _uiStateFlow.value = _uiStateFlow.value.copy(mediaItemWithSchedules = it)
            }
        }
    }

    fun handleInputEvent(inputEvent: DetailsInputEvent) {
        try {
            _uiStateFlow.value = when (inputEvent) {
                is FollowInputEvent -> followEventHandler.handleEvent(
                    inputEvent,
                    _uiStateFlow.value,
                    viewModelScope,
                    airingRepository
                )
                else -> throw IllegalArgumentException("Unsupported input event of type ${inputEvent::class.simpleName}")
            }
        } catch (e: IllegalArgumentException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onError(ErrorItem.ofThrowable(e))
        }
    }

    private fun onError(errorItem: ErrorItem?) {
        _uiStateFlow.value = _uiStateFlow.value.copy(errorItem = errorItem)
    }
}