package com.iskorsukov.aniwatcher.ui.base

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow

open class SearchableMediaViewModel(
    airingRepository: AiringRepository
): FollowableMediaViewModel(airingRepository) {

    protected val searchTextFlow: MutableStateFlow<String> = MutableStateFlow("")

    fun onSearchTextChanged(searchText: String) {
        searchTextFlow.value = searchText
    }

    protected fun <T> filterMediaFlow(map: Map<MediaItem, T>, searchText: String): Map<MediaItem, T> {
        return if (searchText.length < 4) {
            map
        } else {
            map.filterKeys { mediaItem ->
                mediaItem.genres.joinToString().contains(searchText, true)
                        || mediaItem.title.containsIgnoreCase(searchText)

            }
        }
    }
}