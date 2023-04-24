package com.iskorsukov.aniwatcher.ui.airing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.ui.base.util.filterFormatMediaFlow
import com.iskorsukov.aniwatcher.ui.format.FilterFormatDialogState
import com.iskorsukov.aniwatcher.ui.format.rememberFilterFormatDialogState

@Composable
fun rememberAiringScreenState(
    uiStateWithData: AiringUiStateWithData,
    mediaItemMapper: MediaItemMapper,
    filterFormatDialogState: FilterFormatDialogState = rememberFilterFormatDialogState()
): AiringScreenState {
    return remember(
        uiStateWithData,
        mediaItemMapper,
        filterFormatDialogState
    ) {
        AiringScreenState(
            uiStateWithData = uiStateWithData,
            filterFormatDialogState = filterFormatDialogState,
            mediaItemMapper = mediaItemMapper
        )
    }
}

class AiringScreenState(
    val uiStateWithData: AiringUiStateWithData,
    val filterFormatDialogState: FilterFormatDialogState,
    val mediaItemMapper: MediaItemMapper
) {

    private val currentDayOfWeekLocal = DateTimeHelper.currentDayOfWeek()

    val mediaWithNextAiringMap: Map<DayOfWeekLocal, List<Pair<AiringScheduleItem, MediaItem>>>
        get() {
            return uiStateWithData.mediaWithSchedulesMap
                .let {
                    filterFormatMediaFlow(
                        it,
                        filterFormatDialogState.deselectedFormats
                    )
                }
                .let {
                    mediaItemMapper.groupAiringSchedulesByDayOfWeek(
                        it,
                        uiStateWithData.timeInMinutes
                    ).toSortedMap { first, second ->
                        var firstDiff = first.ordinal - currentDayOfWeekLocal.ordinal
                        if (firstDiff < 0) firstDiff += 7
                        var secondDiff = second.ordinal - currentDayOfWeekLocal.ordinal
                        if (secondDiff < 0) secondDiff += 7
                        firstDiff - secondDiff
                    }
                }
        }

    val shouldShowResetButton: Boolean
        get() {
            return filterFormatDialogState.deselectedFormats.isNotEmpty()
        }

    fun reset() {
        filterFormatDialogState.deselectedFormats.clear()
    }
}