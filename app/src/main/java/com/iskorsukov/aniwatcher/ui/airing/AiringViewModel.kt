package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    private val currentDayOfWeekLocal = DayOfWeekLocal.ofCalendar(Calendar.getInstance())

    val airingSchedulesByDayOfWeekFlow by lazy {
        airingRepository.mediaWithSchedulesFlow.map {
            MediaItemMapper.groupAiringSchedulesByDayOfWeek(it).toSortedMap { first, second ->
                var firstDiff = first.ordinal - currentDayOfWeekLocal.ordinal
                if (firstDiff < 0) firstDiff += 7
                var secondDiff = second.ordinal - currentDayOfWeekLocal.ordinal
                if (secondDiff < 0) secondDiff += 7
                firstDiff - secondDiff
            }
        }.distinctUntilChanged()
    }

    fun loadAiringData() {
        val year = DateTimeHelper.currentYear(Calendar.getInstance())
        val season = DateTimeHelper.currentSeason(Calendar.getInstance())
        viewModelScope.launch { airingRepository.loadSeasonAiringData(year, season) }
    }
}