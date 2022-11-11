package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.FollowableMediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AiringViewModel @Inject constructor(
    private val airingRepository: AiringRepository
): FollowableMediaViewModel(airingRepository) {

    private val currentDayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

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
}