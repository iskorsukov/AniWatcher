package com.iskorsukov.aniwatcher.ui.base.viewmodel

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
}