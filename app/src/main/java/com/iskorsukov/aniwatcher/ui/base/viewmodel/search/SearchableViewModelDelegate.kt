package com.iskorsukov.aniwatcher.ui.base.viewmodel.search

import com.iskorsukov.aniwatcher.domain.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SearchableViewModelDelegate @Inject constructor(): SearchableViewModel {

    private val _searchTextFlow = MutableStateFlow("")
    val searchTextFlow: StateFlow<String> = _searchTextFlow

    override fun onSearchTextChanged(searchText: String) {
        _searchTextFlow.value = searchText
    }

    fun <T> filterMediaFlow(map: Map<MediaItem, T>, searchText: String): Map<MediaItem, T> {
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