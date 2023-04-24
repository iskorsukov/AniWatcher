package com.iskorsukov.aniwatcher.ui.following

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.FormatsFilterUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.ResetStateUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SearchTextUiState
import com.iskorsukov.aniwatcher.ui.base.viewmodel.event.SortingOptionsUiState
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

data class FollowingUiStateWithData(
    val mediaWithSchedulesMap: Map<MediaItem, List<AiringScheduleItem>> = emptyMap(),
    val timeInMinutes: Long = 0L,
    val errorItem: ErrorItem? = null,
)