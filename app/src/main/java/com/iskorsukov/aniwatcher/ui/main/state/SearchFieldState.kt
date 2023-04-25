package com.iskorsukov.aniwatcher.ui.main.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberSearchFieldState(): SearchFieldState {
    return remember {
        SearchFieldState()
    }
}

class SearchFieldState {
    var searchText by mutableStateOf("")
    var searchFieldOpen by mutableStateOf(false)

    fun appendText(appendText: String) {
        searchFieldOpen = true
        searchText = if (searchText.isBlank()) {
            appendText
        } else {
            "$searchText $appendText"
        }
    }

    fun reset() {
        searchText = ""
        searchFieldOpen = false
    }
}