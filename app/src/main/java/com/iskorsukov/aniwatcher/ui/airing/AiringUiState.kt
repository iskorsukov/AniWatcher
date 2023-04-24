package com.iskorsukov.aniwatcher.ui.airing

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

data class AiringUiStateWithData(
    val mediaWithSchedulesMap: Map<MediaItem, List<AiringScheduleItem>> = emptyMap(),
    val timeInMinutes: Long = 0L,
    val errorItem: ErrorItem? = null
)