package com.iskorsukov.aniwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    private val _refreshingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val refreshingState: StateFlow<Boolean> = _refreshingState

    fun loadAiringData() {
        _refreshingState.value = true
        val year = DateTimeHelper.currentYear(Calendar.getInstance())
        val season = DateTimeHelper.currentSeason(Calendar.getInstance())
        viewModelScope.launch {
            try {
                airingRepository.loadSeasonAiringData(year, season)
                _refreshingState.value = false
            } catch (e: Exception) {
                _refreshingState.value = false
                return@launch
            }
        }
    }

}