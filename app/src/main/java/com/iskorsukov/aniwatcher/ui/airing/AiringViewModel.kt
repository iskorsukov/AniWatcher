package com.iskorsukov.aniwatcher.ui.airing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.AiringSchedulesMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.Comparator

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): ViewModel() {

    val airingSchedulesByDayOfWeekFlow by lazy {
        airingRepository.mediaWithSchedulesFlow.map {
            AiringSchedulesMapper.groupAiringSchedulesByDayOfWeek(it).toSortedMap()
        }.distinctUntilChanged()
    }

    val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = Calendar.getInstance().timeInMillis
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.distinctUntilChanged()

    fun loadAiringData() {
        val year = DateTimeHelper.currentYear(Calendar.getInstance())
        val season = DateTimeHelper.currentSeason(Calendar.getInstance())
        viewModelScope.launch { airingRepository.loadSeasonAiringData(year, season) }
    }
}