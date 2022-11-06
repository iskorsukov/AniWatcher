package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.FollowableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): FollowableMediaViewModel(airingRepository) {

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