package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.ui.following.FollowingUiState
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption
import org.junit.Test

class SortingOptionEventHandlerTest {
    private val sortingOptionEventHandler: SortingOptionEventHandler<FollowingUiState> = SortingOptionEventHandler()

    @Test
    fun handleEvent_sortingOptionChanged() {
        val uiState = sortingOptionEventHandler.handleEvent(
            SortingOptionChangedInputEvent(SortingOption.SCORE),
            FollowingUiState.DEFAULT
        )

        assertThat(uiState.sortingOption).isEqualTo(SortingOption.SCORE)
    }
}