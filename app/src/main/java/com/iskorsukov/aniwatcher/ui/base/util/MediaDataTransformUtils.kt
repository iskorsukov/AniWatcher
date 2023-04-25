package com.iskorsukov.aniwatcher.ui.base.util

import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption

fun <T> filterFormatMediaFlow(map: Map<MediaItem, T>, deselectedFormats: List<MediaItem.LocalFormat>): Map<MediaItem, T> {
    return map.filterKeys { mediaItem -> !deselectedFormats.contains(mediaItem.format) }
}

fun <T> filterSearchMediaFlow(map: Map<MediaItem, T>, searchText: String): Map<MediaItem, T> {
    val searchTokens = searchText.split(" ", ", ", ",").filter { it.length > 3 }
    return if (searchText.length < 4) {
        map
    } else {
        map.filterKeys { mediaItem ->
            searchTokens.all {
                mediaItem.genres.joinToString().contains(it, true)
                        || mediaItem.title.containsIgnoreCase(it)
            }
        }
    }
}

fun sortMediaFlow(map: Map<MediaItem, AiringScheduleItem?>, sortingOption: SortingOption): Map<MediaItem, AiringScheduleItem?> {
    return map.toSortedMap { first, second ->
        sortingOption.comparator.compare(first to map[first], second to map[second])
    }
}