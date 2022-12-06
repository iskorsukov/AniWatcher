package com.iskorsukov.aniwatcher.ui.base.viewmodel.sort

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption

interface SortableViewModel {

    fun onSortingOptionChanged(sortingOption: SortingOption)

    fun sortMediaFlow(map: Map<MediaItem, AiringScheduleItem?>, sortingOption: SortingOption): Map<MediaItem, AiringScheduleItem?> {
        return map.toSortedMap { first, second ->
            sortingOption.comparator.compare(first to map[first], second to map[second])
        }
    }
}