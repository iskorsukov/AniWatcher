package com.iskorsukov.aniwatcher.ui.base

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import kotlinx.coroutines.flow.MutableStateFlow

open class SortableMediaViewModel(
    airingRepository: AiringRepository
): SearchableMediaViewModel(airingRepository) {

    protected val sortingOptionFlow: MutableStateFlow<SortingOption> = MutableStateFlow(SortingOption.AIRING_AT)

    fun onSortingOptionChanged(sortingOption: SortingOption) {
        sortingOptionFlow.value = sortingOption
    }

    // TODO Refactor - This is horrific
    protected fun <T> sortMediaFlow(map: Map<MediaItem, T>, sortingOption: SortingOption): Map<MediaItem, T> {
        return map.toSortedMap(sortingOption.comparator ?: Comparator { first: MediaItem, second: MediaItem ->
            val firstValue = map[first]
            val secondValue = map[second]
            val firstMostRecent = if (firstValue is AiringScheduleItem) {
                firstValue
            } else if (firstValue is List<*> && firstValue.size > 0 && firstValue.first() is AiringScheduleItem) {
                (firstValue as List<AiringScheduleItem>).reduce { firstAiring, secondAiring ->
                    if (firstAiring.airingAt < secondAiring.airingAt) firstAiring
                    else secondAiring
                }
            } else {
                null
            }
            val secondMostRecent: AiringScheduleItem? = if (secondValue is AiringScheduleItem) {
                secondValue
            } else if (secondValue is List<*> && secondValue.size > 0 && secondValue.first() is AiringScheduleItem) {
                (secondValue as List<AiringScheduleItem>).reduce { firstAiring, secondAiring ->
                    if (firstAiring.airingAt < secondAiring.airingAt) firstAiring
                    else secondAiring
                }
            } else {
                null
            }
            val firstMostRecentAiringAt = firstMostRecent?.airingAt ?: Int.MAX_VALUE
            val secondMostRecentAiringAt = secondMostRecent?.airingAt ?: Int.MAX_VALUE
            return@Comparator firstMostRecentAiringAt - secondMostRecentAiringAt
        })
    }
}